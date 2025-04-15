package starship.fishhelper.mixin;

import net.minecraft.client.network.message.MessageHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import starship.fishhelper.fishmessage.FishMessage;
import net.minecraft.text.Text;


@Mixin(MessageHandler.class)
public abstract class MixinChatListener {
//    @Inject(method = "onChatMessage", at = @At("HEAD"))
//    private void cacheChatData(SignedMessage message, GameProfile sender,
//                               MessageType.Parameters params, CallbackInfo ci){
//        Fishmessage.getInstance().handleChatMsg(message, sender, params);
//    }
    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void maybeCancelMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (FishMessage.getInstance().shouldChatMsgCancel(message)) {
            ci.cancel();
        }
    }
    @ModifyVariable(
            method = "onGameMessage",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Text modifyGameMessage(Text original) {
        return FishMessage.getInstance().sendChatMsg(original);
    }




}
