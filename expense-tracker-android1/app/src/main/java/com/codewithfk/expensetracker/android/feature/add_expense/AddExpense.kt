@file:OptIn(ExperimentalMaterial3Api::class)

package com.codewithfk.expensetracker.android.feature.add_expense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.codewithfk.expensetracker.android.R
import com.codewithfk.expensetracker.android.base.AddExpenseNavigationEvent
import com.codewithfk.expensetracker.android.base.NavigationEvent
import com.codewithfk.expensetracker.android.data.model.ExpenseEntity
import com.codewithfk.expensetracker.android.ui.theme.InterFontFamily
import com.codewithfk.expensetracker.android.ui.theme.LightGrey
import com.codewithfk.expensetracker.android.ui.theme.Typography
import com.codewithfk.expensetracker.android.utils.Utils
import com.codewithfk.expensetracker.android.widget.ExpenseTextView

@Composable
fun AddExpense(
    navController: NavController,
    isIncome: Boolean,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val menuExpanded = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                NavigationEvent.NavigateBack -> navController.popBackStack()
                AddExpenseNavigationEvent.MenuOpenedClicked -> menuExpanded.value = true
                else -> {}
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, card, topBar) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.ic_topbar),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(nameRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { viewModel.onEvent(AddExpenseUiEvent.OnBackPressed) }
                )

                ExpenseTextView(
                    text = "Add ${if (isIncome) "Income" else "Expense"}",
                    style = Typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )

                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Image(
                        painter = painterResource(id = R.drawable.dots_menu),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable {
                                viewModel.onEvent(AddExpenseUiEvent.OnMenuClicked)
                            }
                    )

                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { ExpenseTextView(text = "Profile") },
                            onClick = { menuExpanded.value = false }
                        )
                        DropdownMenuItem(
                            text = { ExpenseTextView(text = "Settings") },
                            onClick = { menuExpanded.value = false }
                        )
                    }
                }
            }

            DataForm(
                modifier = Modifier.constrainAs(card) {
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onAddExpenseClick = {
                    viewModel.onEvent(AddExpenseUiEvent.OnAddExpenseClicked(it))
                },
                isIncome = isIncome
            )
        }
    }
}

@Composable
fun DataForm(
    modifier: Modifier,
    onAddExpenseClick: (model: ExpenseEntity) -> Unit,
    isIncome: Boolean
) {
    val name = remember { mutableStateOf("") }
    val amount = remember { mutableStateOf("") }
    val date = remember { mutableLongStateOf(0L) }
    val dateDialogVisibility = remember { mutableStateOf(false) }
    val type = remember { mutableStateOf(if (isIncome) "Income" else "Expense") }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TitleComponent(title = "Name")

        ExpenseDropDown(
            listOfItems = if (isIncome)
                listOf("Salary", "Freelance", "Bonus", "Investment")
            else
                listOf("Grocery", "Netflix", "Rent", "Shopping", "Food", "Bills"),
            onItemSelected = { name.value = it }
        )

        Spacer(modifier = Modifier.size(24.dp))
        TitleComponent("Amount")

        OutlinedTextField(
            value = amount.value,
            onValueChange = { newValue ->
                amount.value = newValue.filter { it.isDigit() || it == '.' }
            },
            textStyle = TextStyle(color = Color.Black),
            visualTransformation = { text ->
                val out = "₹${text.text}"
                val currencyOffsetTranslator = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int = offset + 1
                    override fun transformedToOriginal(offset: Int): Int =
                        if (offset > 0) offset - 1 else 0
                }
                TransformedText(AnnotatedString(out), currencyOffsetTranslator)
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { ExpenseTextView(text = "Enter amount") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.size(24.dp))
        TitleComponent("Date")

        OutlinedTextField(
            value = if (date.longValue == 0L)
                ""
            else
                Utils.formatDateToHumanReadableForm(date.longValue),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { dateDialogVisibility.value = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
            ),
            placeholder = { ExpenseTextView(text = "Select date") }
        )

        Spacer(modifier = Modifier.size(24.dp))
        Button(
            onClick = {
                val model = ExpenseEntity(
                    id = null,
                    title = name.value,
                    amount = amount.value.toDoubleOrNull() ?: 0.0,
                    date = Utils.formatDateToHumanReadableForm(date.longValue),
                    type = type.value
                )
                onAddExpenseClick(model)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            ExpenseTextView(
                text = "Add ${if (isIncome) "Income" else "Expense"}",
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }

    if (dateDialogVisibility.value) {
        ExpenseDatePickerDialog(
            onDateSelected = {
                date.longValue = it
                dateDialogVisibility.value = false
            },
            onDismiss = { dateDialogVisibility.value = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDatePickerDialog(
    onDateSelected: (date: Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis ?: 0L

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDateSelected(selectedDate) }) {
                ExpenseTextView(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                ExpenseTextView(text = "Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun TitleComponent(title: String) {
    ExpenseTextView(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = LightGrey
    )
    Spacer(modifier = Modifier.size(10.dp))
}

@Composable
fun ExpenseDropDown(listOfItems: List<String>, onItemSelected: (item: String) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val selectedItem = remember { mutableStateOf(listOfItems.first()) }

    ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = it }) {
        OutlinedTextField(
            value = selectedItem.value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            textStyle = TextStyle(fontFamily = InterFontFamily, color = Color.Black),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )

        ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            listOfItems.forEach {
                DropdownMenuItem(
                    text = { ExpenseTextView(text = it) },
                    onClick = {
                        selectedItem.value = it
                        onItemSelected(selectedItem.value)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddExpense() {
    AddExpense(rememberNavController(), true)
}
