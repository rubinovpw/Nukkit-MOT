package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.blockproperty.BlockProperties;
import cn.nukkit.block.blockproperty.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Angelic47
 * Nukkit Project
 */
public class BlockFurnaceBurning extends BlockSolidMeta {

    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.CARDINAL_DIRECTION);

    private static final int[] faces = {2, 5, 3, 4};

    public BlockFurnaceBurning() {
        this(0);
    }

    public BlockFurnaceBurning(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BURNING_FURNACE;
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Burning Furnace";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getLightLevel() {
        return 13;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.FURNACE)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntity.createBlockEntity(BlockEntity.FURNACE, this.getChunk(), nbt);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityFurnace furnace;
            if (t instanceof BlockEntityFurnace) {
                furnace = (BlockEntityFurnace) t;
            } else {
                CompoundTag nbt = new CompoundTag()
                        .putList(new ListTag<>("Items"))
                        .putString("id", BlockEntity.FURNACE)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                furnace = (BlockEntityFurnace) BlockEntity.createBlockEntity(BlockEntity.FURNACE, this.getChunk(), nbt);
            }

            if (furnace.namedTag.contains("Lock") && furnace.namedTag.get("Lock") instanceof StringTag) {
                if (!furnace.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            player.addWindow(furnace.getInventory());
        }

        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(FURNACE));
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    this.toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityFurnace) {
            return ContainerInventory.calculateRedstone(((BlockEntityFurnace) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
