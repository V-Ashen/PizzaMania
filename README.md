*PizzaMania - Android Pizza Ordering App*

PizzaMania is a comprehensive Android application designed to simulate a complete, multi-branch pizza ordering and delivery ecosystem. The project features three distinct user roles: Customer, Staff, and Driver, each with a dedicated interface and functionality.

The application is built with an offline-first architecture, utilizing a sophisticated local SQLite database to manage all multi-branch logic, menu availability, and the entire order lifecycle. It also integrates modern Android technologies including Google Maps for location services and Firebase for cloud-based photo reviews.

Key Features
The application is divided into three modules based on user roles:

👤 Customer Features
User Authentication: Secure registration and login for customers.
Automatic Branch Assignment: Automatically detects the user's GPS location and assigns them to the nearest branch (Colombo or Galle).
Dynamic Menu: Displays only the menu items that are currently marked as "available" by the staff at the assigned branch.
Auto-Sliding Banner: An animated, auto-scrolling banner showcases featured items.
Shopping Cart: Add/remove items and adjust quantities before checkout.
Order Placement: Places orders which are saved locally and tagged to the specific branch.
Order History: View a list of all past orders.
Photo Reviews: Submit reviews for past orders, including a written comment and a photo taken with the phone's camera. Reviews are saved to Firebase Firestore.

👨‍🍳 Staff Features
Role-Based Login: Dedicated logins for staff of each branch (e.g., staffcolombo, staffgalle).
Branch-Specific Dashboard: Staff can only view and manage orders and menus for their assigned branch.
Order Approval: View a list of "Pending" orders and approve them with a single tap.
Real-time Menu Management: Toggle the availability of any menu item for their branch using a simple switch. Changes are instantly reflected for customers.
View Customer Reviews: Access a feed of all customer reviews, including photos and comments, fetched from Firebase Firestore.

🚚 Driver Features
Role-Based Login: Dedicated logins for drivers of each branch.
Branch-Specific Dashboard: Drivers can only see "Approved" orders ready for pickup from their assigned branch.
Google Maps Navigation: On selecting an order, the app launches Google Maps with turn-by-turn navigation to the customer's address.


Technology Stack & Architecture

*Language: Java
*UI: Android SDK with Material Design components, RecyclerView, DrawerLayout.
*Local Database: SQLite. The entire multi-branch logic, including menu items, user accounts, availability status, and orders, is managed through a complex local database. This demonstrates a robust offline-first architecture.
*Cloud Service: Firebase Firestore is used exclusively to store and retrieve all customer reviews and photos.
*APIs & Services:
-Google Maps SDK for Android: Used for displaying branch locations and launching navigation.
-Google Play Services Location API: Used to get the device's current location for branch assignment



Architectural Design Choice

This project intentionally uses a local SQLite database as the primary backend to simulate a multi-user, multi-branch system. This design choice was made to fulfill the coursework requirements of building a complex, self-contained Android application without relying on a live, paid backend server.
The DBHelper.java class is the core of this system, managing all business logic including:
-Seeding initial data for multiple branches and their distinct menus.
-Calculating the nearest branch using the Haversine formula on stored coordinates.
-Filtering menus and orders based on the logged-in user's assigned branch



Setup and Installation

1.Clone the repository:
git clone https://github.com/V-Ashen/pizzamania.git

2.Open in Android Studio: Open the project in Android Studio.

3.Google Maps API Key:
Go to the Google Cloud Console.
Enable the "Maps SDK for Android".
Create an API key.
Open the app/src/main/res/values/google_maps_api.xml file (or AndroidManifest.xml where your key is stored) and paste your key.

4.Firebase Setup: The project contains a google-services.json file. To connect to your own Firebase project, replace this file with the one you download from your Firebase project console.

5.Build and Run: Build the project and run it on an Android emulator or a physical device.

Usage & Test Credentials
Customer: Register a new account through the app.
Staff (Colombo): username staff, password staff123
Staff (Galle): username staffgalle, password staff123
Driver (Colombo): username driver, password driver123
Driver (Galle): username drivergalle, password driver123
