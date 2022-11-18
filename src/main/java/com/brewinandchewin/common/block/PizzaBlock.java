package com.brewinandchewin.common.block;

import com.brewinandchewin.core.registry.BCItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PizzaBlock extends Block
{
	public static final IntegerProperty SERVINGS = IntegerProperty.create("servings", 0, 3);

	protected static final VoxelShape[] SHAPES = new VoxelShape[]{
			Block.box(0.0D, 0.0D, 0.0D, 8.0D, 2.0D, 8.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 8.0D),
			Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 8.0D), Block.box(0.0D, 0.0D, 8.0D, 8.0D, 2.0D, 16.0D)),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
	};

	/*
	 * This block provides up to 4 servings of food to players who interact with it.
	 * If a leftover item is specified, the block lingers at 0 servings, and is destroyed on right-click.
	 *
	 * @param properties   Block properties.
	 * @param servingItem  The meal to be served.
	 * @param hasLeftovers Whether the block remains when out of servings. If false, the block vanishes once it runs out.
	 */
	public PizzaBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(SERVINGS, 3));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(SERVINGS)];
	}
	
	/*@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}*/

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (worldIn.isClientSide) {
			if (this.takeServing(worldIn, pos, state, player, handIn).consumesAction()) {
				return InteractionResult.SUCCESS;
			}
		}

		return this.takeServing(worldIn, pos, state, player, handIn);
	}

	private InteractionResult takeServing(LevelAccessor worldIn, BlockPos pos, BlockState state, Player player, InteractionHand handIn) {
		int servings = state.getValue(SERVINGS);
		ItemStack serving = new ItemStack(BCItems.PIZZA_SLICE.get(), 1);
		ItemStack heldStack = player.getItemInHand(handIn);
		if (!player.getInventory().add(serving)) {
			player.drop(serving, false);
		} if (true) {
			if (worldIn.getBlockState(pos).getValue(SERVINGS) == 0) {
				worldIn.removeBlock(pos, false);
			} else if (worldIn.getBlockState(pos).getValue(SERVINGS) > 0) {
				worldIn.setBlock(pos, state.setValue(SERVINGS, servings - 1), 3);
			}
		}
		worldIn.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		return facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.below()).getMaterial().isSolid();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SERVINGS);
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return blockState.getValue(SERVINGS);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
		return false;
	}
}