package com.thecopperrail;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CopperRailBlock extends PoweredRailBlock {
    public CopperRailBlock(Properties settings){
        super(settings);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.INVERTED, false));
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
	    boolean isInverted = switch(state.getValue(getShapeProperty())) {
            case EAST_WEST -> ctx.getHorizontalDirection() == Direction.EAST;
            case NORTH_SOUTH -> ctx.getHorizontalDirection() == Direction.SOUTH;
            default -> throw new UnsupportedOperationException();
        };
        return state.setValue(BlockStateProperties.INVERTED, isInverted);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, WATERLOGGED, BlockStateProperties.INVERTED);
    }

    /** @return Normalized push direction assuming powered. */
    static public Vec3 getPushForce(BlockState railState) {
        Vec3 pushForce = switch(railState.getValue(SHAPE)){
            case ASCENDING_EAST, ASCENDING_WEST, EAST_WEST -> new Vec3(1,0,0);
            case ASCENDING_SOUTH, ASCENDING_NORTH, NORTH_SOUTH -> new Vec3(0,0,1);
            default -> throw new UnsupportedOperationException();
        };
        if(railState.getValue(BlockStateProperties.INVERTED))
            pushForce = pushForce.reverse();
        return pushForce;
    }
}