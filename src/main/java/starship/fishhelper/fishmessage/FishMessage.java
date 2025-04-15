package starship.fishhelper.fishmessage;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import starship.fishhelper.MCCIFishHelper;
import net.minecraft.network.message.MessageType;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.message.SignedMessage;
import starship.fishhelper.mixin.MixinChatHudAccessor;
import java.time.Instant;
import java.util.*;
import java.util.regex.*;


public class FishMessage {
    private static FishMessage instance;
    private final MCCIFishHelper fishHelper;
    private static MinecraftClient client;
    private static ChatHud chatHud;
    private List<ChatHudLine.Visible> chatVisibleMessages;
    private List<ChatHudLine> chatMessages;

    private final boolean ifDebug = false;

    private final FishSession session = new FishSession();
    private static final Pattern CAUGHT_PATTERN = Pattern.compile("\\(\uE138\\) You caught: \\[(.+?)](?: x(\\d+))?\\s*$");
    private static final Pattern TRIGGER_PATTERN = Pattern.compile("\uE012 (Triggered|Special): (.+?)");
    private static final Pattern XP_PATTERN = Pattern.compile("\uE012 You earned: (\\d+) Island XP");

    private static final Set<String> KNOWN_TRIGGER_NAMES = Set.of(
            "Speedy Rod", "Boosted Rod", "Graceful Rod", "Stable Rod", "Glitched Rod",
            "XP Magnet", "Fish Magnet", "Pearl Magnet", "Treasure Magnet", "Spirit Magnet",
            "Elusive Catch", "Supply Preserve"
    );
    private String chatHistoryFishMessage = "";

    public FishMessage(MCCIFishHelper fishhelper) {
        this.fishHelper = fishhelper;
        FishMessage.instance = this;
        MCCIFishHelper.logger.info("Fishmessage class created");
    }

    public static FishMessage getInstance() {
        return instance;
    }

    public void tick(MinecraftClient client) {
        if(client.player != null && client.world != null){

            FishMessage.client = client;
            chatHud = FishMessage.client.inGameHud.getChatHud();
            chatVisibleMessages = ((MixinChatHudAccessor) chatHud).getVisibleMessages();
            chatMessages = ((MixinChatHudAccessor) chatHud).getMessages();
//            client.player.sendMessage(Text.of("isActive: "+session.isActive
//                                                    +" catchTime: "+session.catchTime
//                                                    +" vm size: "+chatVisibleMessages.size()
//                                                    +" m size: "+chatMessages.size()), true);
//            MCCIFishHelper.logger.info("caught chat message: {}", session.caughtMessage);
        }
    }


    public Text sendChatMsg(Text text) {
        if(ifDebug) return text;
        if(client.player == null || client.world == null) return text;
        String tx = text.getString();
        if(session.isActive) {
//            if(session.isLast) return text;
            MutableText t = session.caughtMessage.copy();
            String msg = t.getString();
//            MutableText ts = Text.literal("tets");
//            if (session.isLast) session.reset();
            return t;
        }
        else
            return text;

    }

    public boolean shouldChatMsgCancel(Text text) {
        if(ifDebug) return false;
        if(client.player == null || client.world == null) return false;

        String msg = text.getString();

        Matcher caughtMatcher = CAUGHT_PATTERN.matcher(msg);
        Matcher triggerMatcher = TRIGGER_PATTERN.matcher(msg);
        Matcher earnedMatcher = XP_PATTERN.matcher(msg);
        if (session.isActive && (Util.getMeasuringTimeMs() - session.catchTime) > 1000*3) session.reset();

        if (caughtMatcher.find() && !session.isActive) {
            session.isActive = true;

            session.catchTime = Util.getMeasuringTimeMs();
            session.lootName = caughtMatcher.group(1).trim();
            String countStr = caughtMatcher.group(2);
            session.lootCount = (countStr != null) ? Integer.parseInt(countStr) : 1;
            session.catType = session.extraCategoryFromName(session.lootName);

            session.caughtMessage = extractCaughtMessage(text.copy());
//            session.caughtMessage = text.copy();
//            MCCIFishHelper.logger.info("caught message right now{}", text);
            return false;

        }

        if (triggerMatcher.find() && session.isActive) {
            Text icon = extractTriggerIcon(text);
            if (chatVisibleMessages == null || chatMessages == null) return true;
            for (int i = 0; i < Math.min(5, chatVisibleMessages.size()); i++) {
                if (chatMessages.get(i).content().getString().contains(session.caughtMessage.getString())) {
                    session.caughtMessage.append(Text.literal(" ")).append(icon);
//                    ChatHudLine newLine = new ChatHudLine(chatMessages.get(i).creationTick(),
//                                                        session.caughtMessage,
//                                                        chatMessages.get(i).signature(),
//                                                        chatMessages.get(i).indicator());
                    chatVisibleMessages.remove(i);
                    chatMessages.remove(i);
//                    ((MixinChatHudAccessor) chatHud).invokeAddVisibleMessage(newLine);
                    break;
                }
            }

            return false;
        }

        if (earnedMatcher.find() && session.isActive) {
            session.isLast = true;
            session.xpGained = Integer.parseInt(earnedMatcher.group(1).trim());
            session.reset();
            return true;
        }
        return false;
    }

    public boolean shouldHistoryChatCancel(Text text) { // false = no change
        if(ifDebug) return false;
        if (chatVisibleMessages != null || chatMessages != null) return false;
//        MCCIFishHelper.logger.info("it did run here!");
        String plain = text.getString();
//        MCCIFishHelper.logger.info("plain: " + plain);
        Pattern pattern = Pattern.compile(".*?(\\(.*?\\) You caught: .+)");
        Matcher matcher = pattern.matcher(plain);
        if (matcher.find()) {
            String x = matcher.group(1);
            if (Objects.equals(chatHistoryFishMessage, ""))
                chatHistoryFishMessage = matcher.group(1);
            else // the second or third time fishmsg shows
                if (matcher.group(1).contains(chatHistoryFishMessage) || matcher.group(1).equals(chatHistoryFishMessage)) {
//                    MCCIFishHelper.logger.info("it shoudld be canceled!");
                    return true;
                }
                else // new history msg
                    chatHistoryFishMessage = matcher.group(1);
        }
        else chatHistoryFishMessage = "";
        return false;
    }

    public Text extractTriggerIcon(Text fullText) {
        String plain = fullText.getString();
        if (!plain.contains("\uE012")) {
            return null;
        }

        Optional<String> matchedName = KNOWN_TRIGGER_NAMES.stream().filter(plain::contains).findFirst();

        return matchedName.map(FontFactory::get).orElse(Text.empty());
    }

    public MutableText extractCaughtMessage(Text fullText) {
        String msg = fullText.getString();

        boolean ifFound = false;
        MutableText root = Text.empty();
        for (Text msg1 : fullText.getSiblings()) {
            String str1 = msg1.getString();
            if (!str1.contains(""))
                root.append(msg1);
            else{
                MutableText root1 = Text.empty();
                for (Text msg2 : msg1.getSiblings()) {
                    String str2 = msg2.getString();

                    if (!str2.contains("")) root1.append(msg2);
                    else {
                        if (str2.equals("")){
                            ifFound = true;
                            root1.append(FontFactory.getCategory(session.catType));
//                            break;
                        }
                        if (ifFound) {
                            root1.append(Text.literal(") You caught:").setStyle(Style.EMPTY.withColor(0x23D106)));
                            break;
                        }
                        MutableText root2 = Text.empty();
                        for (Text msg3 : msg2.getSiblings()) {
                            String str3 = msg3.getString();
                            if (!str3.equals(""))
                                root2.append(msg3);
                            else {
                                root2.append(FontFactory.getCategory(session.catType));
                            }
                        }
                        String root2str = root2.getString();
//                        root1.append(FontFactory.getCategory(FontFactory.CategoryType.JUNK));
                        root1.append(root2);
                    }
                }
                String root1str = root1.getString();
                root.append(root1);
            }

        }
        return root;
    }

    public MutableText _extractCaughtMessage(Text fullText) {
        String msg = fullText.getString();

        boolean ifFound = false;
        MutableText root = Text.empty();
        for (Text msg1 : fullText.getSiblings()) {
            String str1 = msg1.getString();
            if (!str1.contains(""))
                root.append(msg1);
            else{
                MutableText root1 = Text.empty();
                for (Text msg2 : msg1.getSiblings()) {
                    String str2 = msg2.getString();

                    if (!str2.contains("")) root1.append(msg2);
                    else {
                        if (str2.equals("")){
                            ifFound = true;
                            root1.append(Text.literal("(").setStyle(Style.EMPTY.withColor(0x23D106)))
                                .append(Text.literal("").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withFont(
                                        Identifier.of("fish-helper", "icon"))));
//                            break;
                        }
                        if (ifFound) {
                            root1.append(Text.literal(") You caught:").setStyle(Style.EMPTY.withColor(0x23D106)));
                            break;
                        }
                        MutableText root2 = Text.empty();
                        for (Text msg3 : msg2.getSiblings()) {
                            String str3 = msg3.getString();
                            if (!str3.equals(""))
                                root2.append(msg3);
                            else {
                                root2.append(Text.literal("("))
                                .append(Text.literal("").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withFont(
                                    Identifier.of("fish-helper", "icon"))));
                            }
                        }
                        String root2str = root2.getString();
//                        root1.append(FontFactory.getCategory(FontFactory.CategoryType.JUNK));
                        root1.append(root2);
                    }
                }
                String root1str = root1.getString();
                root.append(root1);
            }

        }
        return root;
    }

    public void handleChatMsg(SignedMessage message, GameProfile sender, MessageType.Parameters params) {
        Text text = message.getContent();
        Instant time = message.getTimestamp();
//        MCCIFishHelper.logger.info("Chat message received: {}", text);
    }

}
