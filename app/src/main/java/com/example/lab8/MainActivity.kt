package com.example.lab8

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lab8.ui.theme.Lab8Theme
import kotlinx.serialization.Serializable

// Data classes
data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val image: String
)

data class Location(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
)

class CharacterDb {
    private val characters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        ),
        Character(
            id = 2,
            name = "Morty Smith",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
        ),
        Character(
            id = 3,
            name = "Summer Smith",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Female",
            image = "https://rickandmortyapi.com/api/character/avatar/3.jpeg"
        ),
        Character(
            id = 4,
            name = "Beth Smith",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Female",
            image = "https://rickandmortyapi.com/api/character/avatar/4.jpeg"
        ),
        Character(
            id = 5,
            name = "Jerry Smith",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            image = "https://rickandmortyapi.com/api/character/avatar/5.jpeg"
        ),
        Character(
            id = 19,
            name = "Abadango Cluster Princess",
            status = "Alive",
            species = "Alien",
            type = "",
            gender = "Female",
            image = "https://rickandmortyapi.com/api/character/avatar/19.jpeg"
        )
    )

    fun getAllCharacters(): List<Character> = characters
    fun getCharacterById(id: Int): Character? = characters.find { it.id == id }
}

class LocationDb {
    private val locations = listOf(
        Location(
            id = 1,
            name = "Earth (C-137)",
            type = "Planet",
            dimension = "Dimension C-137"
        ),
        Location(
            id = 3,
            name = "Abadango",
            type = "Cluster",
            dimension = "unknown"
        ),
        Location(
            id = 7,
            name = "Citadel of Ricks",
            type = "Space station",
            dimension = "unknown"
        ),
        Location(
            id = 8,
            name = "Worldender's lair",
            type = "Planet",
            dimension = "unknown"
        ),
        Location(
            id = 9,
            name = "Anatomy Park",
            type = "Microverse",
            dimension = "Dimension C-137"
        ),
        Location(
            id = 11,
            name = "Interdimensional Cable",
            type = "TV",
            dimension = "unknown"
        )
    )

    fun getAllLocations(): List<Location> = locations
    fun getLocationById(id: Int): Location? = locations.find { it.id == id }
}

@Serializable
object LoginDestination

@Serializable
object MainDestination

@Serializable
object CharactersListDestination

@Serializable
data class CharacterDetailDestination(val characterId: Int)

@Serializable
object LocationsListDestination

@Serializable
data class LocationDetailDestination(val locationId: Int)

@Serializable
object ProfileDestination

// Bottom Navigation Items
sealed class BottomNavItem(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    data object Characters : BottomNavItem(
        title = "Characters",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    data object Locations : BottomNavItem(
        title = "Locations",
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn
    )

    data object Profile : BottomNavItem(
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab8Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LoginDestination,
        modifier = modifier
    ) {
        composable<LoginDestination> {
            LoginScreen(
                onStartClick = {
                    navController.navigate(MainDestination) {
                        popUpTo<LoginDestination> { inclusive = true }
                    }
                }
            )
        }

        composable<MainDestination> {
            MainScreen(
                onLogout = {
                    navController.navigate(LoginDestination) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedTab == 0) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Characters"
                        )
                    },
                    label = { Text("Characters") },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        navController.navigate(CharactersListDestination) {
                            popUpTo(CharactersListDestination) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedTab == 1) Icons.Filled.LocationOn else Icons.Outlined.LocationOn,
                            contentDescription = "Locations"
                        )
                    },
                    label = { Text("Locations") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate(LocationsListDestination) {
                            popUpTo(LocationsListDestination) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedTab == 2) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile") },
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        navController.navigate(ProfileDestination) {
                            popUpTo(ProfileDestination) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = CharactersListDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Characters List
            composable<CharactersListDestination> {
                CharactersScreen(
                    onCharacterClick = { characterId ->
                        navController.navigate(CharacterDetailDestination(characterId))
                    },
                    onBackPressed = {
                        (context as? Activity)?.finish()
                    }
                )
            }

            // Character Detail
            composable<CharacterDetailDestination> { backStackEntry ->
                val args = backStackEntry.toRoute<CharacterDetailDestination>()
                CharacterDetailScreen(
                    characterId = args.characterId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Locations List
            composable<LocationsListDestination> {
                LocationsScreen(
                    onLocationClick = { locationId ->
                        navController.navigate(LocationDetailDestination(locationId))
                    },
                    onBackPressed = {
                        (context as? Activity)?.finish()
                    }
                )
            }

            // Location Detail
            composable<LocationDetailDestination> { backStackEntry ->
                val args = backStackEntry.toRoute<LocationDetailDestination>()
                LocationDetailScreen(
                    locationId = args.locationId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Profile
            composable<ProfileDestination> {
                ProfileScreen(
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun LoginScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.rick_morty_logo),
            contentDescription = "Rick & Morty Logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Empezar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Jorge Andrés Villeda Solís - #24932",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    onCharacterClick: (Int) -> Unit,
    onBackPressed: () -> Unit
) {
    val characterDb = remember { CharacterDb() }
    val characters = remember { characterDb.getAllCharacters() }

    BackHandler {
        onBackPressed()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Characters") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(characters) { character ->
                CharacterItem(
                    character = character,
                    onClick = { onCharacterClick(character.id) }
                )
            }
        }
    }
}

@Composable
fun CharacterItem(
    character: Character,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.image)
                    .crossfade(true)
                    .build(),
                contentDescription = character.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${character.species} • ${character.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = character.gender,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    onBackClick: () -> Unit
) {
    val characterDb = remember { CharacterDb() }
    val character = remember { characterDb.getCharacterById(characterId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Character Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        character?.let { char ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(char.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = char.name,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = char.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                DetailRow(label = "Species:", value = char.species)
                DetailRow(label = "Status:", value = char.status)
                DetailRow(label = "Gender:", value = char.gender)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    onLocationClick: (Int) -> Unit,
    onBackPressed: () -> Unit
) {
    val locationDb = remember { LocationDb() }
    val locations = remember { locationDb.getAllLocations() }

    BackHandler {
        onBackPressed()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locations") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(locations) { location ->
                LocationItem(
                    location = location,
                    onClick = { onLocationClick(location.id) }
                )
            }
        }
    }
}

@Composable
fun LocationItem(
    location: Location,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = location.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailScreen(
    locationId: Int,
    onBackClick: () -> Unit
) {
    val locationDb = remember { LocationDb() }
    val location = remember { locationDb.getLocationById(locationId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        location?.let { loc ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = loc.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                DetailRow(label = "ID:", value = loc.id.toString())
                DetailRow(label = "Type:", value = loc.type)
                DetailRow(label = "Dimensions:", value = loc.dimension)
            }
        }
    }
}

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        DetailRow(label = "Nombre:", value = "Jorge Andrés Villeda Solís")
        DetailRow(label = "Carné:", value = "24932")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = "Cerrar sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
    }
}