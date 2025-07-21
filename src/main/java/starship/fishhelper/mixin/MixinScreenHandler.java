package starship.fishhelper.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import starship.fishhelper.MCCIFishHelper;
import starship.fishhelper.trevorOpener.TrevorOpener;

@Mixin(ScreenHandler.class)
public abstract class MixinScreenHandler {
    @Inject(method = "internalOnSlotClick", at = @At("HEAD"))
    private void onSlotClick(int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (player.getWorld().isClient) {
//            MCCIFishHelper.logger.info("[DEBUG] Slot Clicked: slotId={}, button={}, action={}", slotId, button, actionType);
            if (slotId >= 0 && slotId < 100) { // click inside gui // 48 49 50
                if (TrevorOpener.getInstance().ifSUMMAEYScreenOpened && ( slotId == 48 || slotId == 49 || slotId == 50))
                    TrevorOpener.getInstance().detectScreenSUMMARYClose();
                Slot slot = ((ScreenHandler)(Object)this).getSlot(slotId);
                ItemStack itemStack = slot.getStack();
                if (itemStack.isEmpty()) return;
                if (!TrevorOpener.getInstance().treasure.namePattern.matcher(itemStack.getName().getString()).find()) return;

                if (actionType == SlotActionType.QUICK_MOVE && button == 0) { // left+shift click
//                    MCCIFishHelper.logger.info(">> Shift + Left Click detected on slot: {}, {}", itemStack.getName().getString(), itemStack.getCount());
                    TrevorOpener.getInstance().recordTreasure(itemStack.getName().getString(), itemStack.getCount());
                }

                if (actionType == SlotActionType.PICKUP && button == 0) { // left click
//                    MCCIFishHelper.logger.info(">> Left Click detected on slot: {}, {}", itemStack.getName().getString(), itemStack.getCount());
                    TrevorOpener.getInstance().recordTreasure(itemStack.getName().getString(), 1);

                }
            }

        }
    }
}
