# hw1- Manual Review

The homework will be based on this project named "Expense Tracker",where users will be able to add/remove daily transaction. 

## Compile

To compile the code from terminal, use the following command:
```
cd src
javac ExpenseTrackerApp.java
java ExpenseTracker
```

You should be able to view the GUI of the project upon successful compilation. 

## Java Version
This code is compiled with ```openjdk 17.0.7 2023-04-18```. Please update your JDK accordingly if you face any incompatibility issue.

## New Feature
Undo Functionality
Indicators: Users can easily tell when undo is allowed/disallowed through clear visual indicators.
Removing Transactions: Users can remove any transaction by selecting the respective row in the table.
Updates: The removal of a transaction is instantly reflected in both the Transactions List and the Total Cost.

## New test
Changes Made
1.Introduction of ErrorHandler Interface: We introduced an ErrorHandler interface to abstract the way errors are reported in the application. This change allows us to inject different error handling strategies for different contexts (e.g., production, testing).

2.Refactoring of ExpenseTrackerController: The ExpenseTrackerController class was refactored to accept an ErrorHandler instance. This enables the controller to report errors via the injected ErrorHandler, improving testability and separation of concerns.

3. New Test Cases:
1. Add Transaction
   Steps: Add a transaction with an amount of 50.00 and a category of “food”.
   Expected Output: The transaction is added to the table, and the Total Cost is updated accordingly.
2. Invalid Input Handling
   Steps: Attempt to add a transaction with an invalid amount (e.g., negative value) or an empty category.
   Expected Output: Error messages are displayed, and the transactions list and Total Cost remain unchanged.
3. Filter by Amount
   Steps: Add multiple transactions with varying amounts, then apply a filter based on a specific amount.
   Expected Output: Only transactions matching the specified amount filter are displayed (and highlighted if applicable).
4. Filter by Category
   Steps: Add multiple transactions with different categories, then apply a category filter.
   Expected Output: Only transactions matching the specified category filter are displayed (and highlighted if applicable).
5. Undo Disallowed
   Steps: Attempt to perform an undo operation when the transactions list is empty.
   Expected Output: The UI widget for undoing transactions is either disabled, or an error message/exception is generated.
6. Undo Allowed
   Steps: Add a transaction, then undo the addition.
   Expected Output: The transaction is removed from the table, and the Total Cost is updated to reflect this change.
   
New Test Implementations:
TestErrorHandler: A test-specific implementation of the ErrorHandler interface that allows for checking whether an error was reported in unit tests.