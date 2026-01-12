package com.example.klientutveckling_projekt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * SharedViewModel klassen som kopplar alla fragments logik
 *
 * Ansvarar för spellogiken, skapa med ViewModelFactory och injecera en repository
 */
class SharedViewModel(
    private val repository: ClickRepository
) : ViewModel() {

private companion object{
    private const val MAX_OFFLINEPROGRESS: Long = 8*60*60
}
    private val _musicVolume = MutableStateFlow(0.5f)
    val musicVolume: StateFlow<Float> = _musicVolume.asStateFlow()

    init {
        applyOfflineProgress()
        startPassiveIncome()
    }

    public val allUpgrades = listOf(
        Upgrade(1, "Calloused Hands", "Years of digging toughen your grip", 1.05, 10.0),
        Upgrade(2, "Sharpened Shovel", "A cleaner edge cuts through soil faster", 1.1, 25.0),
        Upgrade(3, "Steel Pickaxe", "Break through compact ground", 1.15, 50.0),
        Upgrade(4, "Reinforced Boots", "Better footing for stronger strikes", 1.2, 90.0),
        Upgrade(5, "Miner’s Gloves", "Grip reduces wasted effort", 1.25, 150.0),

        Upgrade(6, "Hand Crank Drill", "Manual drilling improves efficiency", 1.3, 250.0),
        Upgrade(7, "Hardened Drill Bit", "Cuts through rock layers", 1.35, 400.0),
        Upgrade(8, "Steam-Powered Drill", "Industrial-era digging power", 1.4, 650.0),
        Upgrade(9, "Pressure Valves", "Sustained drilling without slowdown", 1.45, 1_000.0),
        Upgrade(10, "Early Automation", "Basic machinery works on its own", 1.0, 1_500.0, 0.5),

        Upgrade(11, "Electric Drill Rig", "Consistent high-speed drilling", 1.6, 2_200.0),
        Upgrade(12, "Diamond-Tipped Bit", "Pierces the toughest rock", 1.7, 3_200.0),
        Upgrade(13, "Hydraulic Pistons", "Massive force per strike", 1.8, 4_500.0),
        Upgrade(14, "Subsurface Scanner", "Find weak spots underground", 1.9, 6_000.0),
        Upgrade(15, "Automated Drill Arm", "Machines dig even while you rest", 1.0, 8_000.0, 1.5),

        Upgrade(16, "Tunnel Stabilizers", "Deeper digs without collapse", 2.0, 11_000.0),
        Upgrade(17, "Plasma Cutter", "Melts rock on contact", 2.15, 15_000.0),
        Upgrade(18, "Nano-Coated Bits", "Near-zero friction drilling", 2.3, 20_000.0),
        Upgrade(19, "AI Dig Optimization", "Perfect drilling angles every time", 2.45, 27_000.0),
        Upgrade(20, "Self-Replicating Drones", "Autonomous swarm drilling", 1.0, 35_000.0, 3.0),

        Upgrade(21, "Magma-Resistant Hull", "Dig safely near the mantle", 2.6, 45_000.0),
        Upgrade(22, "Seismic Resonator", "Shatter rock with vibrations", 2.8, 60_000.0),
        Upgrade(23, "Core Pressure Adaptors", "Survive crushing depths", 3.0, 80_000.0),
        Upgrade(24, "Antimatter Drill Head", "Erase matter effortlessly", 3.3, 110_000.0),
        Upgrade(25, "Planetary Bore Engine", "Industrial-scale planetary drilling", 1.0, 150_000.0, 6.0),

        Upgrade(26, "Graviton Stabilizer", "Ignore gravity’s limits", 3.6, 200_000.0),
        Upgrade(27, "Dimensional Excavator", "Dig between layers of reality", 4.0, 275_000.0),
        Upgrade(28, "Core Extraction Matrix", "Harvest energy from the planet’s heart", 4.5, 375_000.0),
        Upgrade(29, "Singularity Drill", "Infinite pressure at a single point", 5.0, 500_000.0),
        Upgrade(30, "Worldbreaker Protocol", "The planet yields completely", 1.0, 750_000.0, 12.0)
    )


    val purchasedUpgrades: StateFlow<Set<Int>> = repository.purchasedUpgrades
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val multiplier: StateFlow<Double> = purchasedUpgrades
        .map{purchasedIds ->
            allUpgrades.filter {it.id in purchasedIds}.fold(1.0){acc, upgrade ->
                acc *upgrade.multiplier
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 1.0
        )

    val meters: StateFlow<Double> = repository.meters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0.0
        )

    val metersPerSecond: StateFlow<Double> = repository.metersPerSeconds.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0.0
    )

    /**
     * Ändrar _musicVolume som är Mutable för att updatera dess read-only counterpart
     */
    fun setMusicVolume(volume: Float){
        _musicVolume.value = volume.coerceIn(0f, 1f)
    }

    /**
     * Körs i Init block
     *
     * Startar klockan för passiv inkomst, mängden är baserad på uppgraderingar
     */
    private fun startPassiveIncome() {
        viewModelScope.launch {
            while (true){
                delay(1000)
                val mps = metersPerSecond.value
                if (mps > 0) {
                    repository.addMeters(mps)
                }
            }
        }
    }

    /**
     * Lägger till den mängd meter som användaren har tjänat medan dem är offline
     *
     *
     */
    private fun applyOfflineProgress(){
        viewModelScope.launch {
            try {
                val lastActive = repository.getLastActiveTime()
                val now = System.currentTimeMillis()
                if (lastActive <= 0L){
                    repository.setLastActiveTime(now)
                    return@launch
                }

                val rawSecondsOffline = (now - lastActive) / 1000.0
                if (rawSecondsOffline <= 0.0){
                    repository.setLastActiveTime(now)
                    return@launch
                }

                val secondsOffline = Math.min(rawSecondsOffline, MAX_OFFLINEPROGRESS.toDouble())

                val mps = repository.getMetersPerSecondOnce()

                val earned = secondsOffline * mps

                if (earned > 0.0){
                    repository.addMeters(earned)
                }

                repository.setLastActiveTime(now)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    /**
     * Funktionen som kallas när användare klickar på spelytan
     *
     * Delegerar vidare till ClickRepository för persistent lagring
     */
    fun click() {
        viewModelScope.launch {
            repository.addMeters(multiplier.value)
        }
    }

    /**
     * Kallas när användare försöker köpa en upgradering
     *
     * Har felhantering för felaktig mängd av valuta samt om uppgradering redan är köpt
     *
     * Delegerar till ClickRepository för persistent lagring
     *
     * @property upgrade Tar emot en typ av Upgrade
     * @return Returnerar en Boolean om köpet gick igenom
     */
    fun buyUpgrade(upgrade: Upgrade): Boolean {
        if (meters.value < upgrade.cost) return false
        if (upgrade.id in purchasedUpgrades.value) return false

        viewModelScope.launch {
            repository.subtractMeters(upgrade.cost)
            repository.addPurchasedUpgrade(upgrade.id)

            if (upgrade.metersPerSecondBonus > 0) {
                repository.addMetersPerSecond(upgrade.metersPerSecondBonus)
            }
        }


        return true
    }

    /**
     * Delegerar till ClickRepositorys reset metod
     */
    suspend fun reset() {


        repository.reset()

    }
}
