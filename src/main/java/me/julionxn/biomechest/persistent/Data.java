package me.julionxn.biomechest.persistent;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Data {

    private final HashMap<String, List<String>> data = new HashMap<>(){{
        put("default", new ArrayList<>());
    }};

    /**
     * @param world El mundo actual.
     * @param blockPos La posición del bloque del bioma.
     * @return En caso de ser un bioma que se encuentre en la configuración, y contener opciones válidas,
     * se regresa el identificador de una de esas lootables al azar. En caso de no contener ningún bioma
     * en la configuración, se regresa una opción de las lootables por default, en caso de contener
     * opciones válidas.
     */
    public Optional<Identifier> getRandomLootable(World world, BlockPos blockPos){
        Optional<RegistryKey<Biome>> optionalRegistryKey = world.getBiome(blockPos).getKey();
        if (optionalRegistryKey.isEmpty()) return Optional.empty();
        Identifier biomeId = optionalRegistryKey.get().getValue();
        return getRandomLootable(world, biomeId);
    }


    /**
     * @param world El mundo actual.
     * @param biomeId El identificador del bioma.
     * @return En caso de ser un bioma que se encuentre en la configuración, y contener opciones válidas,
     * se regresa el identificador de una de esas lootables al azar. En caso de no contener ningún bioma
     * en la configuración, se regresa una opción de las lootables por default, en caso de contener
     * opciones válidas.
     */
    public Optional<Identifier> getRandomLootable(World world, Identifier biomeId){
        String biomeIdStr = biomeId.toString();
        if (!data.containsKey(biomeIdStr)){
            return getRandomIdentifierFromBiome(world, "default");
        }
        return getRandomIdentifierFromBiome(world, biomeIdStr);
    }

    /**
     * @param world El mundo actual.
     * @param biomeId El identificador del bioma.
     * @return En caso de ser un bioma que se encuentre en la configuración, y contener opciones válidas,
     * se regresa el identificador de una de esas lootables al azar.
     */
    private Optional<Identifier> getRandomIdentifierFromBiome(World world, String biomeId){
        Optional<String> randomIdStrOptional = randomItem(world, data.get(biomeId));
        if (randomIdStrOptional.isEmpty()) {
            return Optional.empty();
        }
        return parseItem(randomIdStrOptional.get());
    }

    /**
     * @param id Un id en formato namespace:item
     * @return En caso de ser una operación exitosa, se devuelve el identificador parseado.
     */
    private Optional<Identifier> parseItem(String id){
        Identifier randomId = Identifier.tryParse(id);
        if (randomId == null) {
            return Optional.empty();
        }
        return Optional.of(randomId);
    }

    /**
     * @param world El mundo actual
     * @param options Una lista de opciones
     * @return Un item al azar de la lista de opciones
     */
    private Optional<String> randomItem(World world, List<String> options){
        if (options.isEmpty()){
            return Optional.empty();
        }
        int index = world.random.nextInt(options.size());
        return Optional.of(options.get(index));
    }

    /**
     * @return Los biomas que se cargaron en la configuración
     */
    public Set<String> getBiomes(){
        return data.keySet();
    }

    /**
     * @param biome El bioma
     * @return La lista con los lootables relacionados con ese bioma
     */
    @Nullable
    public List<String> getOptionsOf(String biome){
        return data.get(biome);
    }

}
