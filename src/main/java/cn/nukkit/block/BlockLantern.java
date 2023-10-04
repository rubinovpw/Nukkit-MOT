package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.blockproperty.BlockProperties;
import cn.nukkit.block.blockproperty.BooleanBlockProperty;
import cn.nukkit.block.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import org.jetbrains.annotations.NotNull;

public class BlockLantern extends BlockFlowable {

    public static final BooleanBlockProperty HANGING = new BooleanBlockProperty("hanging", false);

    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.HANGING);

    public BlockLantern() {
        this(0);
    }

    public BlockLantern(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LANTERN;
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Lantern";
    }

    private boolean isBlockAboveValid() {
        Block support = this.up();
        switch (support.getId()) {
            case IRON_BARS:
            case HOPPER_BLOCK:
                return true;
            default:
                if (support instanceof BlockFence) {
                    return true;
                }
                if (support instanceof BlockSlab && (support.getDamage() & 0x08) == 0x00) {
                    return true;
                }
                if (support instanceof BlockStairs && (support.getDamage() & 0x04) == 0x00) {
                    return true;
                }
                return !support.isTransparent() && support.isSolid() && !support.isPowerSource();
        }
    }

    private boolean isBlockUnderValid() {
        Block down = this.down();
        if (down instanceof BlockLeaves) {
            return false;
        } else if (down instanceof BlockFence || down instanceof BlockWall) {
            return true;
        } else if (down instanceof BlockSlab) {
            return (down.getDamage() & 0x08) == 0x08;
        } else if (down instanceof BlockStairs) {
            return (down.getDamage() & 0x04) == 0x04;
        } else if (down.isSolid()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if(this.getLevelBlock() instanceof BlockLiquid || this.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
            return false;
        }

        boolean isUnderValid = this.isBlockUnderValid();
        boolean hanging = face != BlockFace.UP && this.isBlockAboveValid() && (!isUnderValid || face == BlockFace.DOWN);
        if (!isUnderValid && !hanging) {
            return false;
        }

        if (hanging) {
            this.setDamage(1);
        } else {
            this.setDamage(0);
        }

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getDamage() == 0) {
                if (!this.isBlockUnderValid()) {
                    level.useBreakOn(this);
                }
            } else if (!this.isBlockAboveValid()) {
                level.useBreakOn(this);
            }
            return type;
        }
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @Override
    public double getHardness() {
        return 5.0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getMinX() {
        return x + (5.0/16);
    }

    @Override
    public double getMinY() {
        return y + (this.getDamage()==0?0: 1./16);
    }

    @Override
    public double getMinZ() {
        return z + (5.0/16);
    }

    @Override
    public double getMaxX() {
        return x + (11.0/16);
    }

    @Override
    public double getMaxY() {
        return y + (this.getDamage()==0? 7.0/16 : 8.0/16);
    }

    @Override
    public double getMaxZ() {
        return z + (11.0/16);
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockLantern());
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

}
