package me.julionxn.biomechest.mixin;

import me.julionxn.biomechest.persistent.Config;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends Block {

	@Unique
	private static final BooleanProperty USED = BooleanProperty.of("used");

	public ChestBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void init(AbstractBlock.Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, CallbackInfo ci) {
		setDefaultState(getStateManager().getDefaultState().with(USED, true));
	}

	@Inject(method = "appendProperties", at = @At("TAIL"))
	public void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci){
		builder.add(USED);
	}

	@Inject(method = "onPlaced", at = @At("TAIL"))
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci){
		//En caso de que un jugador coloque un cofre, y esté en creativo, aplicar la propiedad USED a false
		if (placer instanceof PlayerEntity player && player.isCreative()){
			world.setBlockState(pos, state.with(USED, false));
			Config.getInstance().getData().getRandomLootable(world, pos).ifPresent(id ->
				LootableContainerBlockEntity.setLootTable(world, world.random, pos, id)
			);
		}
	}

	@Inject(method = "onUse", at = @At("TAIL"))
	public void injectInventory(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir){
		if (state.get(USED)) return;
		world.setBlockState(pos, state.with(USED, true));
	}

}