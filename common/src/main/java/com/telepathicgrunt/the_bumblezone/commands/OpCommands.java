package com.telepathicgrunt.the_bumblezone.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.BzRegisterCommandsEvent;
import com.telepathicgrunt.the_bumblezone.items.essence.EssenceOfTheBees;
import com.telepathicgrunt.the_bumblezone.modcompat.BumblezoneAPI;
import com.telepathicgrunt.the_bumblezone.modinit.BzDimension;
import com.telepathicgrunt.the_bumblezone.modules.PlayerDataHandler;
import com.telepathicgrunt.the_bumblezone.modules.base.ModuleHelper;
import com.telepathicgrunt.the_bumblezone.modules.registry.ModuleRegistry;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OpCommands {
    private static final ResourceKey<Registry<Registry<?>>> ROOT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("root"));
    private static MinecraftServer currentMinecraftServer = null;
    private static Set<String> cachedSuggestion = new HashSet<>();

    enum DATA_BOOLEAN_WRITE_ARG {
        IS_BEE_ESSENCED
    }

    enum DATA_READ_ARG {
        IS_BEE_ESSENCED,
        QUEENS_DESIRED_CRAFTED_BEEHIVE,
        QUEENS_DESIRED_BEES_BRED,
        QUEENS_DESIRED_FLOWERS_SPAWNED,
        QUEENS_DESIRED_HONEY_BOTTLE_DRANK,
        QUEENS_DESIRED_BEE_STINGERS_FIRED,
        QUEENS_DESIRED_BEE_SAVED,
        QUEENS_DESIRED_POLLEN_PUFF_HITS,
        QUEENS_DESIRED_HONEY_SLIME_BRED,
        QUEENS_DESIRED_BEES_FED,
        QUEENS_DESIRED_QUEEN_BEE_TRADE,
        QUEENS_DESIRED_KILLED_ENTITY_COUNTER
    }

    public static void createCommand(BzRegisterCommandsEvent commandEvent) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = commandEvent.dispatcher();
        CommandBuildContext buildContext = commandEvent.context();

        String commandTeleportString = "bumblezone_teleport";
        String commandWriteString = "bumblezone_modify_data";
        String commandReadString = "bumblezone_read_data";
        String commandTagLogOutputString = "bumblezone_tag_log_output";
        String dataArg = "data_to_modify";
        String newDataArg = "new_value";
        String entityArg = "entity_to_check";

        LiteralCommandNode<CommandSourceStack> source = commandDispatcher.register(Commands.literal(commandWriteString)
            .requires((permission) -> permission.hasPermission(2))
            .then(Commands.argument(dataArg, StringArgumentType.string())
            .suggests((ctx, sb) -> SharedSuggestionProvider.suggest(methodBooleanWriteSuggestions(ctx), sb))
            .then(Commands.argument("targets", EntityArgument.players())
            .then(Commands.argument(newDataArg, BoolArgumentType.bool())
            .executes(cs -> {
                runBooleanSetMethod(cs.getSource(), cs.getArgument(dataArg, String.class), EntityArgument.getPlayers(cs, "targets"), cs.getArgument(newDataArg, boolean.class), cs);
                return 1;
            })
        ))));

        commandDispatcher.register(Commands.literal(commandWriteString).redirect(source));

        LiteralCommandNode<CommandSourceStack> source2 = commandDispatcher.register(Commands.literal(commandReadString)
            .requires((permission) -> permission.hasPermission(2))
            .then(Commands.argument(dataArg, StringArgumentType.string())
            .suggests((ctx, sb) -> SharedSuggestionProvider.suggest(methodReadSuggestions(ctx), sb))
            .then(Commands.argument("targets", EntityArgument.players())
            .executes(cs -> {
                runReadMethod(cs.getSource(), cs.getArgument(dataArg, String.class), null, EntityArgument.getPlayers(cs, "targets"), cs);
                return 1;
            })
        )));

        commandDispatcher.register(Commands.literal(commandReadString).redirect(source2));


        LiteralCommandNode<CommandSourceStack> source3 = commandDispatcher.register(Commands.literal(commandReadString)
            .requires((permission) -> permission.hasPermission(2))
            .then(Commands.literal(DATA_READ_ARG.QUEENS_DESIRED_KILLED_ENTITY_COUNTER.name().toLowerCase(Locale.ROOT))
            .then(Commands.argument("targets", EntityArgument.players())
            .then(Commands.argument(entityArg, StringArgumentType.string())
            .suggests((ctx, sb) -> SharedSuggestionProvider.suggest(killedSuggestions(EntityArgument.getPlayers(ctx, "targets")), sb))
            .executes(cs -> {
                runReadMethod(cs.getSource(), DATA_READ_ARG.QUEENS_DESIRED_KILLED_ENTITY_COUNTER.name(), cs.getArgument(entityArg, String.class), EntityArgument.getPlayers(cs, "targets"), cs);
                return 1;
            })
        ))));

        commandDispatcher.register(Commands.literal(commandReadString).redirect(source3));

        LiteralCommandNode<CommandSourceStack> source4 = commandDispatcher.register(Commands.literal(commandTeleportString)
            .requires((permission) -> permission.hasPermission(2))
            .then(Commands.argument("targets", EntityArgument.entities())
            .executes(cs -> {
                runTeleportMethod(cs.getSource(), EntityArgument.getEntities(cs, "targets"), cs);
                return 1;
            })
        ));

        commandDispatcher.register(Commands.literal(commandTeleportString).redirect(source4));

        LiteralCommandNode<CommandSourceStack> source5 = commandDispatcher.register(Commands.literal(commandTagLogOutputString)
                .requires((permission) -> permission.hasPermission(2))
                .then(Commands.argument("registry", ResourceKeyArgument.key(ROOT_REGISTRY_KEY))
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(ctx.getSource().registryAccess().listRegistries().map(ResourceKey::location), builder))
                .then(Commands.argument("tag", ResourceLocationArgument.id())
                        .suggests(suggestFromRegistry(r -> r.getTagNames().map(TagKey::location)::iterator, "registry", ROOT_REGISTRY_KEY))
                .executes(cs -> {
                    final ResourceKey<? extends Registry<?>> registryKey = getResourceKey(cs, "registry", ROOT_REGISTRY_KEY).orElseThrow();
                    final Registry<?> registry = cs.getSource().getServer().registryAccess().registry(registryKey).get();
                    final ResourceLocation tagLocation = ResourceLocationArgument.getId(cs, "tag");
                    final TagKey<?> tagKey = TagKey.create(cast(registryKey), tagLocation);
                    final Iterable<? extends Holder<?>> tag = registry.getTagOrEmpty(cast(tagKey));

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("\nTAGSTART");
                    stringBuilder.append("\n{");
                    for (final Holder<?> holder : tag) {
                        stringBuilder.append("\n\t\"");
                        stringBuilder.append(holder.unwrapKey().get().location());
                        stringBuilder.append("\",");
                    }
                    stringBuilder.append("\n}\n");
                    Bumblezone.LOGGER.info(stringBuilder.toString());
                    return 1;
                })
        )));

        commandDispatcher.register(Commands.literal(commandTagLogOutputString).redirect(source5));
    }

    private static Set<String> methodBooleanWriteSuggestions(CommandContext<CommandSourceStack> cs) {
        if (currentMinecraftServer == cs.getSource().getServer()) {
            return cachedSuggestion;
        }

        Set<String> suggestedStrings = new HashSet<>();
        Arrays.stream(DATA_BOOLEAN_WRITE_ARG.values()).forEach(e -> suggestedStrings.add(e.name().toLowerCase(Locale.ROOT)));

        currentMinecraftServer = cs.getSource().getServer();
        cachedSuggestion = suggestedStrings;
        return suggestedStrings;
    }

    private static Set<String> methodReadSuggestions(CommandContext<CommandSourceStack> cs) {
        if (currentMinecraftServer == cs.getSource().getServer()) {
            return cachedSuggestion;
        }

        Set<String> suggestedStrings = new HashSet<>();
        Arrays.stream(DATA_READ_ARG.values()).forEach(e -> suggestedStrings.add(e.name().toLowerCase(Locale.ROOT)));

        currentMinecraftServer = cs.getSource().getServer();
        cachedSuggestion = suggestedStrings;
        return suggestedStrings;
    }


    public static void runBooleanSetMethod(CommandSourceStack commandSourceStack, String dataString, Collection<ServerPlayer> targets, boolean bool, CommandContext<CommandSourceStack> cs) {
        DATA_READ_ARG dataArg;
        try {
            dataArg = DATA_READ_ARG.valueOf(dataString.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            MutableComponent mutableComponent = Component.translatable("command.the_bumblezone.invalid_data_arg");
            commandSourceStack.sendFailure(mutableComponent);
            return;
        }

        if (DATA_READ_ARG.IS_BEE_ESSENCED.equals(dataArg)) {
            for (ServerPlayer targetPlayer : targets) {
                EssenceOfTheBees.setEssence(targetPlayer, bool);
            }
            MutableComponent mutableComponent = Component.translatable("command.the_bumblezone.data_change_success");
            commandSourceStack.sendSuccess(() -> mutableComponent, true);
            return;
        }
    }

    private static Set<String> killedSuggestions(Collection<ServerPlayer> targets) {
        if (targets.isEmpty()) {
            return new HashSet<>();
        }

        AtomicReference<Set<String>> suggestedStrings = new AtomicReference<>(new HashSet<>());
        for (Player player : targets) {
            ModuleHelper.getModule(player, ModuleRegistry.PLAYER_DATA).ifPresent(module ->
                    suggestedStrings.set(
                            module.mobsKilledTracker.keySet()
                                    .stream()
                                    .map(killed -> "\"" + killed.toString() + "\"").
                                    collect(Collectors.toSet())
                    )
            );
        }
        return suggestedStrings.get();
    }

    public static void runReadMethod(CommandSourceStack commandSourceStack, String dataString, String killedString, Collection<ServerPlayer> targets, CommandContext<CommandSourceStack> cs) {
        NonOpCommands.DATA_ARG dataArg;
        try {
            dataArg = NonOpCommands.DATA_ARG.valueOf(dataString.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            MutableComponent mutableComponent = Component.translatable("command.the_bumblezone.invalid_data_arg");
            commandSourceStack.sendFailure(mutableComponent);
            return;
        }

        for (ServerPlayer targetPlayer : targets) {
            if (NonOpCommands.DATA_ARG.IS_BEE_ESSENCED.equals(dataArg)) {
                boolean hasBeeEssence = EssenceOfTheBees.hasEssence(targetPlayer);
                MutableComponent mutableComponent = Component.translatable(
                        hasBeeEssence ?
                                "command.the_bumblezone.have_bee_essence" :
                                "command.the_bumblezone.does_not_have_bee_essence",
                        targetPlayer.getDisplayName()
                );
                commandSourceStack.sendSuccess(() -> mutableComponent, false);
                return;
            }

            if (!PlayerDataHandler.rootAdvancementDone(targetPlayer)) {
                commandSourceStack.sendFailure(Component.translatable("command.the_bumblezone.queens_desired_not_active", targetPlayer.getDisplayName()));
                continue;
            }

            switch (dataArg) {
                case QUEENS_DESIRED_CRAFTED_BEEHIVE -> ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> commandSourceStack.sendSuccess(
                        () -> Component.translatable("command.the_bumblezone.queens_desired_crafted_beehive", targetPlayer.getDisplayName(), capability.craftedBeehives),
                        false));
                case QUEENS_DESIRED_BEES_BRED -> ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> commandSourceStack.sendSuccess(
                        () -> Component.translatable("command.the_bumblezone.queens_desired_bees_bred", targetPlayer.getDisplayName(), capability.beesBred),
                        false));
                case QUEENS_DESIRED_FLOWERS_SPAWNED -> ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> commandSourceStack.sendSuccess(
                        () -> Component.translatable("command.the_bumblezone.queens_desired_flowers_spawned", targetPlayer.getDisplayName(), capability.flowersSpawned),
                        false));
                case QUEENS_DESIRED_HONEY_BOTTLE_DRANK -> ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> commandSourceStack.sendSuccess(
                        () -> Component.translatable("command.the_bumblezone.queens_desired_honey_bottle_drank", targetPlayer.getDisplayName(), capability.honeyBottleDrank),
                        false));
                case QUEENS_DESIRED_BEE_STINGERS_FIRED -> ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> commandSourceStack.sendSuccess(
                        () -> Component.translatable("command.the_bumblezone.queens_desired_bee_stingers_fired", targetPlayer.getDisplayName(), capability.beeStingersFired),
                        false));
                case QUEENS_DESIRED_BEE_SAVED -> ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> commandSourceStack.sendSuccess(
                        () -> Component.translatable("command.the_bumblezone.queens_desired_bee_saved", targetPlayer.getDisplayName(), capability.beeSaved),
                        false));
                case QUEENS_DESIRED_POLLEN_PUFF_HITS -> ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> commandSourceStack.sendSuccess(
                        () -> Component.translatable("command.the_bumblezone.queens_desired_pollen_puff_hits", targetPlayer.getDisplayName(), capability.pollenPuffHits),
                        false));
                case QUEENS_DESIRED_HONEY_SLIME_BRED -> ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> commandSourceStack.sendSuccess(
                        () -> Component.translatable("command.the_bumblezone.queens_desired_honey_slime_bred", targetPlayer.getDisplayName(), capability.honeySlimeBred),
                        false));
                case QUEENS_DESIRED_BEES_FED -> ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> commandSourceStack.sendSuccess(
                        () -> Component.translatable("command.the_bumblezone.queens_desired_bees_fed", targetPlayer.getDisplayName(), capability.beesFed),
                        false));
                case QUEENS_DESIRED_QUEEN_BEE_TRADE -> ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> commandSourceStack.sendSuccess(
                        () -> Component.translatable("command.the_bumblezone.queens_desired_queen_bee_trade", targetPlayer.getDisplayName(), capability.queenBeeTrade),
                        false));
                case QUEENS_DESIRED_KILLED_ENTITY_COUNTER -> {
                    if (killedString != null) {
                        ResourceLocation rl = ResourceLocation.tryParse(killedString);
                        ModuleHelper.getModule(targetPlayer, ModuleRegistry.PLAYER_DATA).ifPresent(capability -> {
                            int killed = capability.mobsKilledTracker.getOrDefault(rl, 0);
                            String translationKey;
                            if (rl.equals(ResourceLocation.fromNamespaceAndPath("minecraft", "ender_dragon"))) {
                                translationKey = "command.the_bumblezone.queens_desired_killed_entity_counter_ender_dragon";
                            } else if (rl.equals(ResourceLocation.fromNamespaceAndPath("minecraft", "wither"))) {
                                translationKey = "command.the_bumblezone.queens_desired_killed_entity_counter_wither";
                            } else {
                                translationKey = "command.the_bumblezone.queens_desired_killed_entity_counter";
                            }

                            if (BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) {
                                commandSourceStack.sendSuccess(() ->
                                                Component.translatable(translationKey,
                                                        targetPlayer.getDisplayName(),
                                                        killed,
                                                        Component.translatable(Util.makeDescriptionId("entity", rl))),
                                        false);
                            } else {
                                commandSourceStack.sendSuccess(() ->
                                                Component.translatable(translationKey,
                                                        targetPlayer.getDisplayName(),
                                                        killed,
                                                        Component.translatable("tag.entity_type." + killedString.replaceAll("[\\\\:/-]", "."))),
                                        false);
                            }
                        });
                    } else {
                        commandSourceStack.sendSuccess(() -> Component.translatable("command.the_bumblezone.invalid_entity_arg"), false);
                        return;
                    }
                }
                default -> {
                }
            }
        }
    }

    public static void runTeleportMethod(CommandSourceStack commandSourceStack, Collection<? extends Entity> targets, CommandContext<CommandSourceStack> cs) {
        for (Entity target : targets) {
            if (target instanceof LivingEntity livingEntity) {
                if (target.level().dimension().equals(BzDimension.BZ_WORLD_KEY)) {
                    BumblezoneAPI.teleportOutOfBz(livingEntity);
                }
                else {
                    BumblezoneAPI.queueEntityForTeleportingToBumblezone(livingEntity);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <O> O cast(final Object input) {
        return (O) input;
    }

    private static <T extends Registry<?>> SuggestionProvider<CommandSourceStack> suggestFromRegistry(
            final Function<Registry<?>, Iterable<ResourceLocation>> namesFunction,
            final String argumentString,
            final ResourceKey<Registry<T>> registryKey) {
        return (ctx, builder) -> getResourceKey(ctx, argumentString, registryKey)
                .flatMap(key -> ctx.getSource().registryAccess().registry(key).map(registry -> {
                    SharedSuggestionProvider.suggestResource(namesFunction.apply(registry), builder);
                    return builder.buildFuture();
                }))
                .orElseGet(builder::buildFuture);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> Optional<ResourceKey<T>> getResourceKey(
            final CommandContext<CommandSourceStack> ctx,
            final String name,
            final ResourceKey<Registry<T>> registryKey) {
        // Don't inline to avoid an unchecked cast warning due to raw types
        final ResourceKey<?> key = ctx.getArgument(name, ResourceKey.class);
        return key.cast(registryKey);
    }
}
