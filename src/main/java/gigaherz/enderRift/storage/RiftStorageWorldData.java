package gigaherz.enderRift.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class RiftStorageWorldData extends WorldSavedData
{
    private static final String IDENTIFIER = "enderRiftStorageManager";

    private Map<Integer, RiftInventory> rifts = new HashMap<Integer, RiftInventory>();
    private int lastRiftId;

    public RiftStorageWorldData() {
        super(IDENTIFIER);
    }

    public static RiftStorageWorldData get(World world)
    {
        MapStorage storage = world.mapStorage;
        RiftStorageWorldData instance = (RiftStorageWorldData)storage.loadData(RiftStorageWorldData.class, IDENTIFIER);
        if (instance == null) {
            instance = new RiftStorageWorldData();
            storage.setData(IDENTIFIER, instance);
        }

        return instance;
    }

    public RiftInventory getRift(int id)
    {
        RiftInventory rift = rifts.get(id);

        if(rift == null)
        {
            rift = new RiftInventory(this);
            rifts.put(id, rift);
        }

        return rift;
    }

    public int getNextRiftId()
    {
        markDirty();

        return ++lastRiftId;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        NBTTagList nbtTagList = nbtTagCompound.getTagList("Rifts", Constants.NBT.TAG_COMPOUND);

        rifts.clear();

        for (int i = 0; i < nbtTagList.tagCount(); ++i)
        {
            NBTTagCompound nbtTagCompound1 = nbtTagList.getCompoundTagAt(i);
            int j = nbtTagCompound1.getByte("Rift");

            RiftInventory inventory = new RiftInventory(this);
            inventory.readFromNBT(nbtTagCompound1);

            rifts.put(j, inventory);
        }

        lastRiftId = nbtTagCompound.getInteger("LastRiftId");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        NBTTagList nbtTagList = new NBTTagList();

        for (Map.Entry<Integer, RiftInventory> entry : rifts.entrySet())
        {
            RiftInventory inventory = entry.getValue();

            NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
            nbtTagCompound1.setInteger("Rift", entry.getKey());
            inventory.writeToNBT(nbtTagCompound1);
            nbtTagList.appendTag(nbtTagCompound1);
        }

        nbtTagCompound.setTag("Rifts", nbtTagList);
        nbtTagCompound.setInteger("LastRiftId", lastRiftId);
    }
}