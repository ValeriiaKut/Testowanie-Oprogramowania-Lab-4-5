package com.example.testowanie4
import DatabaseHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PersonalDataApp()
            }
        }
    }
}

private enum class Screen {
    HOME, ADD, LIST, DELETE
}

@Composable
fun PersonalDataApp() {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var persons by remember { mutableStateOf(dbHelper.getAllPersons()) }
    var statusMessage by remember { mutableStateOf("") }

    fun refreshPersons() {
        persons = dbHelper.getAllPersons()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == Screen.ADD,
                    onClick = {
                        statusMessage = ""
                        currentScreen = Screen.ADD
                    },
                    icon = {},
                    label = { Text("Dodaj osobę") }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.LIST,
                    onClick = {
                        statusMessage = ""
                        refreshPersons()
                        currentScreen = Screen.LIST
                    },
                    icon = {},
                    label = { Text("Lista danych") }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.DELETE,
                    onClick = {
                        statusMessage = ""
                        refreshPersons()
                        currentScreen = Screen.DELETE
                    },
                    icon = {},
                    label = { Text("Usuń osobę") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(
                    onAddClick = {
                        statusMessage = ""
                        currentScreen = Screen.ADD
                    },
                    onListClick = {
                        statusMessage = ""
                        refreshPersons()
                        currentScreen = Screen.LIST
                    },
                    onDeleteClick = {
                        statusMessage = ""
                        refreshPersons()
                        currentScreen = Screen.DELETE
                    }
                )

                Screen.ADD -> AddPersonScreen(
                    onSavePerson = { newPerson ->
                        val insertedId = dbHelper.insertPerson(newPerson)
                        if (insertedId != -1L) {
                            refreshPersons()
                            statusMessage = "Zapisano osobę w bazie SQLite."
                        } else {
                            statusMessage = "Błąd zapisu do bazy."
                        }
                    },
                    onBackClick = {
                        statusMessage = ""
                        currentScreen = Screen.HOME
                    },
                    statusMessage = statusMessage
                )

                Screen.LIST -> PersonListScreen(
                    persons = persons,
                    onBackClick = {
                        currentScreen = Screen.HOME
                    }
                )

                Screen.DELETE -> DeletePersonScreen(
                    persons = persons,
                    onDeletePerson = { personId ->
                        dbHelper.deletePersonById(personId)
                        refreshPersons()
                        statusMessage = "Usunięto rekord."
                    },
                    onBackClick = {
                        statusMessage = ""
                        currentScreen = Screen.HOME
                    },
                    statusMessage = statusMessage
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onListClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onAddClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Dodaj osobę")
        }
        Button(
            onClick = onListClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Wyświetl listę danych")
        }
        Button(
            onClick = onDeleteClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Usuń osobę")
        }
    }
}

@Composable
fun AddPersonScreen(
    onSavePerson: (Person) -> Unit,
    onBackClick: () -> Unit,
    statusMessage: String
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onBackClick) {
                Text("Back")
            }
            Text(
                text = "Dodaj osobę",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Imię") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Nazwisko") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Data urodzenia") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Numer telefonu") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Adres") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSavePerson(
                    Person(
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                        birthDate = birthDate.trim(),
                        phone = phone.trim(),
                        email = email.trim(),
                        address = address.trim()
                    )
                )
                firstName = ""
                lastName = ""
                birthDate = ""
                phone = ""
                email = ""
                address = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Zapisz")
        }

        if (statusMessage.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = statusMessage,
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PersonListScreen(
    persons: List<Person>,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onBackClick) {
                Text("Back")
            }
            Text(
                text = "Lista danych",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (persons.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Brak zapisanych osób.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(persons, key = { it.id }) { person ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${person.firstName} ${person.lastName} (ID: ${person.id})",
                                fontWeight = FontWeight.Bold
                            )
                            Text("Data urodzenia: ${person.birthDate}")
                            Text("Telefon: ${person.phone}")
                            Text("E-mail: ${person.email}")
                            Text("Adres: ${person.address}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeletePersonScreen(
    persons: List<Person>,
    onDeletePerson: (Long) -> Unit,
    onBackClick: () -> Unit,
    statusMessage: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onBackClick) {
                Text("Back")
            }
            Text(
                text = "Usuń osobę",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (statusMessage.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = statusMessage,
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (persons.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Brak rekordów do usunięcia.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(persons, key = { it.id }) { person ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "${person.firstName} ${person.lastName} (ID: ${person.id})",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(person.email)
                                Text(person.phone)
                            }
                            Button(onClick = { onDeletePerson(person.id) }) {
                                Text("Usuń")
                            }
                        }
                    }
                }
            }
        }
    }
}