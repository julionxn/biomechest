package me.julionxn.biomechest.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.julionxn.biomechest.persistent.Config;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
		setDefaultState(getStateManager().getDefaultState().with(USED, false));
	}

	@Inject(method = "appendProperties", at = @At("TAIL"))
	public void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci){
		builder.add(USED);
	}

	@Inject(method = "onPlaced", at = @At("TAIL"))
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci){
		//En caso de que un jugador coloque un cofre, y este no esté en creativo, aplicar la propiedad USED
		if (placer instanceof PlayerEntity player && !player.isCreative()){
			world.setBlockState(pos, state.with(USED, true));
		}
	}

	@Inject(method = "onUse", at = @At("TAIL"))
	public void injectInventory(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir){
		if (world.isClient) return;
		//Verificar que el cofre sea válido para aplicar la lootable
		Inventory inventory = ChestBlock.getInventory((ChestBlock)(Object) this, state, world, pos, true);
		if (state.get(USED)) return;
		if (inventory == null) return;
		if (!inventory.isEmpty()) return;

		//Aplicar la lootable al azar
		Config.getInstance().getData().getRandomLootable(world, pos).ifPresent(id -> {

			/*LootTable test = world.getServer().getLootManager().getTable(id);
			LootContext context = new LootContext.Builder((ServerWorld) world)
					.parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
					.build(LootContextTypes.CHEST);
			test.generateLoot(context).forEach(itemStack -> System.out.println(itemStack.toString()));*/

			LootableContainerBlockEntity.setLootTable(world, world.random, pos, id);
			world.setBlockState(pos, state.with(USED, true));
		});
	}

}