package su.nightexpress.coinsengine.hook;

import net.zithium.deluxecoinflip.api.DeluxeCoinflipAPI;
import net.zithium.deluxecoinflip.economy.provider.EconomyProvider;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public class DeluxeCoinflipHook {

    public static void setup(@NotNull CoinsEnginePlugin plugin) {
        DeluxeCoinflipAPI api = (DeluxeCoinflipAPI) plugin.getPluginManager().getPlugin(HookId.DELUXE_COINFLIP);
        if (api == null) return;

        plugin.getCurrencyManager().getCurrencies().forEach(currency -> {
            Provider provider = new Provider(plugin, currency);
            api.registerEconomyProvider(provider, plugin.getName());
        });
    }

    public static void shutdown() {

    }

    private static class Provider extends EconomyProvider {

        private final CoinsEnginePlugin plugin;
        private final Currency          currency;

        public Provider(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
            super("coinsengine_" + currency.getId());
            this.plugin = plugin;
            this.currency = currency;
        }

        @Override
        public void onEnable() {

        }

        @Nullable
        private CoinsUser getUser(@NotNull OfflinePlayer offlinePlayer) {
            return this.plugin.getUserManager().getUserData(offlinePlayer.getUniqueId());
        }

        @Override
        public double getBalance(OfflinePlayer offlinePlayer) {
            CoinsUser user = this.getUser(offlinePlayer);
            return user == null ? 0 : user.getCurrencyData(this.currency).getBalance();
        }

        @Override
        public void withdraw(OfflinePlayer offlinePlayer, double v) {
            CoinsUser user = this.getUser(offlinePlayer);
            if (user != null) {
                user.getCurrencyData(currency).removeBalance(v);
                plugin.getUserManager().saveAsync(user);
            }
        }

        @Override
        public void deposit(OfflinePlayer offlinePlayer, double v) {
            CoinsUser user = this.getUser(offlinePlayer);
            if (user != null) {
                user.getCurrencyData(currency).addBalance(v);
                plugin.getUserManager().saveAsync(user);
            }
        }
    }
}
