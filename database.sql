DROP DATABASE IF EXISTS budget;
CREATE DATABASE budget;
USE budget; 

CREATE TABLE USERS
(
id INT not null auto_increment PRIMARY KEY,
user_name VARCHAR(100) UNIQUE not null,
pass_word VARCHAR(25) not null,
created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE SAVED_DATA
(
id INT not null auto_increment PRIMARY KEY,
startingAmount DECIMAL(13,4),
startingDate date,
lastMonthVisited INT not null,
user_id INT not null,
created_at TIMESTAMP DEFAULT NOW(),
FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE EXPENSES
(
id INT not null auto_increment PRIMARY KEY,
amount DECIMAL(13,4),
nameType VARCHAR(255),
dateReceived date,
repeatFlag char(1) ,
user_id INT not null,
created_at TIMESTAMP DEFAULT NOW(),
FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE INCOME
(
id INT not null auto_increment PRIMARY KEY,
amount DECIMAL(13,4),
nameType VARCHAR(255),
dateReceived date,
repeatFlag char(1),
user_id INT not null,
created_at TIMESTAMP DEFAULT NOW(),
FOREIGN KEY(user_id) REFERENCES users(id)
);

/*
1. month
2. year
3. day
4. amount
5. user
*/


/*
CREATE TABLE MONTHLY_DATA
(

);
*/

insert into users(user_name,pass_word) VALUES('admin','password');
