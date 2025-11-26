# Introduction

Part Pay was created to make managing payroll and part-time scheduling easier. Designed with small to medium-sized enterprises in mind, it boosts productivity by taking into account the schedules and automatically compute payments. This system makes managing less frequent workers easier by providing an easy-to-use way to manage leave, keep track of hours worked, and make sure payroll is accurate.

### Architecture diagram

<img src= "https://github.com/pavankumarchebelli/PartPay_Demo/blob/main/Screenshots/architecture.png" width =600px height= 337px>

<h3>Main Features: </h3>

1. **User Authentication**:

   - Users can sign up and log in to the application.
   - There are three types of users like Part Time Employee, Manager and Admin
   - Passwords are stored securely in the SQLite database.

2. **TimeSheets**:
   - The application includes a page where employees can clock in and clock out.
   - Users can directly give the clock and clock out time or we can start the timer and end it when shift is completed.

3. **Database**:
   - The application uses a SQLite database to all the employee related information.
   - It includes all the shift details and employee username and password details

4. **PayRoll**:
   - The application also has a payroll generation feature based on the shifts scheduled and their clockout times.
   - Here they can include taxes when generation which can be decided by manager.

5. **Swap and Overtime Requests**
   - In this application an employee can request for a swap based on the manager's approval
   - If they work overtime then they need an overtime approval as well to make it count.

## PartPay Installation

### Prerequisites

- Node Package Manager it wil install along with node [download node](https://nodejs.org/en/download)
- Test if npm is istalled
  ```bash
  npm --version
  ```
- After installing node, install yarn
  ```bash
  npm install --global yarn
  ```
- Clone this project:

  ```bash
  git clone https://github.com/pavankumarchebelli/PartPay_Demo.git
  cd PartPay_Demo
  ```

### Run the project

1. Go to server files and install packages then start the server:
   ```bash
   cd serverFiles/
   npm i
   npm start
   ```
2. The data base is included in the server so no need to start it separately it starts automatically when the server starts..

3. Now open another terminal in the PartPay_Demo folder and go to ui files and install packages:
   ```bash
   yarn
   yarn dev
   ```
4. Access via clicking the link that pops up after running yarn dev.

<h3>Screenshots</h3>

1.	Sign In Page
<img src= "https://github.com/pavankumarchebelli/PartPay_Demo/blob/main/Screenshots/Signin.png" width =600px height= 337px>

2.	Profile Page
<img src= "https://github.com/pavankumarchebelli/PartPay_Demo/blob/main/Screenshots/Profile%20Page.png" width =600px height= 337px>

3.	Generate Payslips
<img src= "https://github.com/pavankumarchebelli/PartPay_Demo/blob/main/Screenshots/GeneratePayslip.png" width =600px height= 337px>

4.	Manage Employee
<img src= "https://github.com/pavankumarchebelli/PartPay_Demo/blob/main/Screenshots/ManageEmployee.png" width =600px height= 337px>

5.	Shift Schedule
<img src= "https://github.com/pavankumarchebelli/PartPay_Demo/blob/main/Screenshots/ShiftSchedule.png" width =600px height= 337px>

6.	Tax Dashboard
<img src= "https://github.com/pavankumarchebelli/PartPay_Demo/blob/main/Screenshots/Tax%20Dashboard.png" width =600px height= 337px>

## Packages and Technologies Used

### UI Package Details:
- **Emotion React & Styled**: Styling frameworks for React.
- **Material-UI**: A React UI framework that provides ready-to-use components.
- **Axios**: Promise based HTTP client for the browser and node.js.
- **React & React-DOM**: Frameworks for building interface components.
- **React Router DOM**: Library for handling routing in React applications.
- **Vite**: A modern, fast front-end build tool.
- **ESLint**: A tool for identifying and reporting on patterns found in ECMAScript/JavaScript code.

### Server Package Details:
- **Express**: Web application framework for Node.js, designed for building web applications and APIs.
- **Bcrypt**: A library to help you hash passwords.
- **CORS**: Package for providing a Connect/Express middleware that can be used to enable CORS with various options.
- **JSON Web Token**: A compact URL-safe means of representing claims to be transferred between two parties.

<h3>Contributions</h3>

Contributions to the PartPay project are welcome. If you find any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.