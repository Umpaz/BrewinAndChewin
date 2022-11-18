package com.brewinandchewin.common.block;

import java.util.function.Supplier;

import com.brewinandchewin.core.utility.BCTextUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.tag.ModTags;

public class RipeCheeseWheelBlock extends Block
{
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
	public static final IntegerProperty SERVINGS = IntegerProperty.create("servings", 0, 3);
	protected static final VoxelShape[] SHAPES = new VoxelShape[]{
			Block.box(2.0D, 0.0D, 2.0D, 8.0D, 6.0D, 8.0D),
			Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 8.0D),
			Shapes.or(Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 8.0D), Block.box(2.0D, 0.0D, 8.0D, 8.0D, 6.0D, 14.0D)),
			Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D),
	};
	public final Supplier<Item> cheeseType;
	
	public RipeCheeseWheelBlock(Supplier<Item> cheeseType, Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(SERVINGS, 3));
		this.cheeseType = cheeseType;
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
		return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, facingState, level, pos, facingPos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return !level.isEmptyBlock(pos.below());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(SERVINGS)];
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		int servings = state.getValue(SERVINGS);
		ItemStack heldStack = player.getItemInHand(handIn);
		
		if (servings > 0) {
			if (heldStack.is(ModTags.KNIVES)) {
				level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
				popResource(level, pos, new ItemStack(cheeseType.get(), 1));
				level.setBlock(pos, state.setValue(SERVINGS, servings - 1), 3);
			} else {
				player.displayClientMessage(BCTextUtils.getTranslation("block.cheese.use_knife"), true);
			}
		}
		if (servings == 0) {
			if (heldStack.is(ModTags.KNIVES)) {
				level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
				popResource(level, pos, new ItemStack(cheeseType.get(), 1));
				level.destroyBlock(pos, false);
			} else {
				player.displayClientMessage(BCTextUtils.getTranslation("block.cheese.use_knife"), true);
			}
		}
		return InteractionResult.SUCCESS;
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