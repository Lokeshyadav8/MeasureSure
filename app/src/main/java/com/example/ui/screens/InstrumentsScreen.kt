package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.OcrExtractionResult
import com.example.data.model.InstrumentEntity
import com.example.data.model.InstrumentStatus
import com.example.data.model.UserRole
import com.example.ui.components.ExpiryStatusBadge
import com.example.ui.components.InstrumentStatusBadge
import com.example.ui.components.RiskScoreBadge
import com.example.ui.theme.*

@Composable
fun InstrumentsScreen(
    instruments: List<InstrumentEntity>,
    userRole: UserRole,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    statusFilter: String,
    onStatusFilterChange: (String) -> Unit,
    categoryFilter: String,
    onCategoryFilterChange: (String) -> Unit,
    onRequestVerification: (InstrumentEntity) -> Unit,
    onViewInstrumentDetails: (InstrumentEntity) -> Unit,
    onRegisterInstrument: (name: String, type: String, category: String, manufacturer: String, model: String, serial: String, capacity: String, unit: String, location: String, tol: Double) -> Unit,
    onRunAiOcrScan: () -> Unit,
    aiOcrResult: OcrExtractionResult?,
    isAiScanning: Boolean,
    modifier: Modifier = Modifier
) {
    var showRegisterDialog by remember { mutableStateOf(false) }

    // Filter instruments
    val filteredInstruments = remember(instruments, searchQuery, statusFilter, categoryFilter) {
        instruments.filter { inst ->
            val matchQuery = searchQuery.isBlank() ||
                    inst.instrumentId.contains(searchQuery, ignoreCase = true) ||
                    inst.name.contains(searchQuery, ignoreCase = true) ||
                    inst.serialNumber.contains(searchQuery, ignoreCase = true) ||
                    inst.manufacturer.contains(searchQuery, ignoreCase = true) ||
                    inst.location.contains(searchQuery, ignoreCase = true) ||
                    inst.type.contains(searchQuery, ignoreCase = true)

            val matchStatus = when (statusFilter) {
                "ALL" -> true
                "VERIFIED" -> inst.status == InstrumentStatus.CERTIFICATE_GENERATED || inst.status == InstrumentStatus.PASSED
                "PENDING" -> inst.status == InstrumentStatus.SUBMITTED || inst.status == InstrumentStatus.ASSIGNED || inst.status == InstrumentStatus.INSPECTION_SCHEDULED || inst.status == InstrumentStatus.UNDER_INSPECTION
                "FAILED" -> inst.status == InstrumentStatus.FAILED
                else -> true
            }

            val matchCategory = categoryFilter == "ALL" || inst.category.equals(categoryFilter, ignoreCase = true)

            matchQuery && matchStatus && matchCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // Search & Registration Top Bar
        Surface(
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search by ID, Serial, Make...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate600) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Slate600)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Slate100,
                            focusedContainerColor = Color.White
                        )
                    )

                    Button(
                        onClick = { showRegisterDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter chips row
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val statusChips = listOf("ALL", "VERIFIED", "PENDING", "FAILED")
                    items(statusChips) { chip ->
                        FilterChip(
                            selected = statusFilter == chip,
                            onClick = { onStatusFilterChange(chip) },
                            label = { Text(chip, fontSize = 11.sp, fontWeight = if (statusFilter == chip) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Slate100
                            )
                        )
                    }
                }
            }
        }

        // Instruments Count Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Registered Instruments (${filteredInstruments.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Navy900
            )

            Text(
                text = "ISO 17025 Registry",
                fontSize = 11.5.sp,
                color = Slate600
            )
        }

        // List of Instruments
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredInstruments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = Slate600, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "No instruments matching criteria", color = Slate600, fontSize = 13.sp)
                        }
                    }
                }
            }

            items(filteredInstruments) { instrument ->
                InstrumentCard(
                    instrument = instrument,
                    userRole = userRole,
                    onRequestVerification = { onRequestVerification(instrument) },
                    onViewDetails = { onViewInstrumentDetails(instrument) }
                )
            }
        }
    }

    if (showRegisterDialog) {
        RegisterInstrumentDialog(
            onDismiss = { showRegisterDialog = false },
            onRegister = { name, type, category, make, model, serial, cap, unit, loc, tol ->
                onRegisterInstrument(name, type, category, make, model, serial, cap, unit, loc, tol)
                showRegisterDialog = false
            },
            onRunAiOcr = onRunAiOcrScan,
            aiOcrResult = aiOcrResult,
            isAiScanning = isAiScanning
        )
    }
}

@Composable
fun InstrumentCard(
    instrument: InstrumentEntity,
    userRole: UserRole,
    onRequestVerification: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetails() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = instrument.instrumentId,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = CyanPrimary
                )

                InstrumentStatusBadge(status = instrument.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Name & Model
            Text(
                text = instrument.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.5.sp,
                color = DarkNavyObsidian
            )

            Text(
                text = "${instrument.type} • ${instrument.manufacturer} ${instrument.modelNumber}",
                fontSize = 12.sp,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Key metadata pill specs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Text(
                        text = "Cap: ${instrument.capacity} ${instrument.unitOfMeasurement}",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkNavyObsidian,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Text(
                        text = "S/N: ${instrument.serialNumber}",
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Slate600,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                RiskScoreBadge(riskLevel = instrument.riskScore)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Location & Expiry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Slate400, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = instrument.location, fontSize = 11.sp, color = Slate500, maxLines = 1)
                }

                ExpiryStatusBadge(validUntilDate = instrument.nextVerificationDate)
            }

            // Action Buttons
            if (instrument.status == InstrumentStatus.DRAFT || instrument.status == InstrumentStatus.FAILED) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRequestVerification,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (instrument.status == InstrumentStatus.FAILED) SlateNavySurface else CyanPrimary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (instrument.status == InstrumentStatus.FAILED) "Request Re-Verification" else "Request Verification",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterInstrumentDialog(
    onDismiss: () -> Unit,
    onRegister: (name: String, type: String, category: String, make: String, model: String, serial: String, cap: String, unit: String, loc: String, tol: Double) -> Unit,
    onRunAiOcr: () -> Unit,
    aiOcrResult: OcrExtractionResult?,
    isAiScanning: Boolean
) {
    val instrumentTypes = listOf(
        "Digital weighing scale",
        "Platform scale",
        "Weighbridge",
        "Retail weighing machine",
        "Petrol pump measuring instrument",
        "Measuring meter",
        "Length measuring instrument",
        "Industrial measurement instrument"
    )

    val categories = listOf("Commercial", "Industrial", "Retail", "Laboratory", "Petroleum")

    var selectedType by remember { mutableStateOf(instrumentTypes[0]) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var name by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var modelNumber by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("30") }
    var unitOfMeasurement by remember { mutableStateOf("kg") }
    var location by remember { mutableStateOf("Apex Central Hub, Station 1") }
    var tolerance by remember { mutableStateOf("0.05") }

    // Auto fill if OCR result arrives
    LaunchedEffect(aiOcrResult) {
        if (aiOcrResult != null) {
            manufacturer = aiOcrResult.manufacturer
            modelNumber = aiOcrResult.model
            serialNumber = aiOcrResult.serialNumber
            selectedType = aiOcrResult.instrumentType
            capacity = aiOcrResult.capacity.replace(Regex("[^0-9.]"), "")
            unitOfMeasurement = aiOcrResult.unit
            name = "${aiOcrResult.manufacturer} ${aiOcrResult.model}"
            tolerance = aiOcrResult.permissibleTolerance.toString()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Register Instrument",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AI OCR Auto-Fill Action Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Navy900
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("AI Nameplate Scanner (OCR)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Auto-extract serial, model, and class", color = Slate300, fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = onRunAiOcr,
                            enabled = !isAiScanning,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            if (isAiScanning) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Scan Specs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Form Scroll Area
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Instrument Name / Label") },
                            placeholder = { Text("e.g. Mettler Lab Precision Scale") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Text(text = "Instrument Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(instrumentTypes) { type ->
                                FilterChip(
                                    selected = selectedType == type,
                                    onClick = { selectedType = type },
                                    label = { Text(type, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    item {
                        Text(text = "Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(categories) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = manufacturer,
                                onValueChange = { manufacturer = it },
                                label = { Text("Manufacturer") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = modelNumber,
                                onValueChange = { modelNumber = it },
                                label = { Text("Model Number") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = serialNumber,
                            onValueChange = { serialNumber = it },
                            label = { Text("Serial Number (S/N)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = capacity,
                                onValueChange = { capacity = it },
                                label = { Text("Capacity") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = unitOfMeasurement,
                                onValueChange = { unitOfMeasurement = it },
                                label = { Text("Unit (kg, g, L, Ton)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Operating Site / Location") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = tolerance,
                            onValueChange = { tolerance = it },
                            label = { Text("Permissible Tolerance (%)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Submit Button
                Button(
                    onClick = {
                        val tolDouble = tolerance.toDoubleOrNull() ?: 0.05
                        onRegister(
                            name.ifBlank { "$manufacturer $selectedType" },
                            selectedType,
                            selectedCategory,
                            manufacturer.ifBlank { "Mettler Toledo" },
                            modelNumber.ifBlank { "X-200" },
                            serialNumber.ifBlank { "SN-" + (10000..99999).random() },
                            capacity,
                            unitOfMeasurement,
                            location,
                            tolDouble
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text("Register & Save to Registry", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
