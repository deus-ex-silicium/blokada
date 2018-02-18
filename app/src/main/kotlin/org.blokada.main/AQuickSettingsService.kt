package org.blokada.main

import android.annotation.TargetApi
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.github.salomonbrys.kodein.instance
import org.blokada.property.State
import org.blokada.R
import org.obsolete.Sync
import org.blokada.presentation.EnabledStateActor
import org.blokada.presentation.IEnabledStateActorListener
import org.obsolete.di

/**
 *
 */
@TargetApi(24)
class AQuickSettingsService : TileService(), IEnabledStateActorListener {

    private val s by lazy { di().instance<State>() }
    private val enabledStateActor by lazy { di().instance<EnabledStateActor>() }
    private var waiting = Sync(false)

    override fun onStartListening() {
        updateTile()
        enabledStateActor.listeners.add(this)
    }

    override fun onStopListening() {
        enabledStateActor.listeners.remove(this)
    }

    override fun onTileAdded() {
        updateTile()
    }

    override fun onClick() {
        if (waiting.get()) return
        s.enabled %= !s.enabled()
        updateTile()
    }

    private fun updateTile() {
        if (qsTile == null) return
        if (s.enabled()) {
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = getString(R.string.main_status_active_recent)
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = getString(R.string.main_status_disabled)
        }
        qsTile.updateTile()
    }

    override fun startActivating() {
        waiting.set(true)
        qsTile.label = getString(R.string.main_status_activating)
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
    }

    override fun finishActivating() {
        waiting.set(false)
        qsTile.label = getString(R.string.main_status_active_recent)
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
    }

    override fun startDeactivating() {
        waiting.set(true)
        qsTile.label = getString(R.string.main_status_deactivating)
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun finishDeactivating() {
        waiting.set(false)
        qsTile.label = getString(R.string.main_status_disabled)
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
