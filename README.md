# 📊 Web Application with Recommendation System using Spring Boot, Thymeleaf, and JavaScript

This is a web-based application developed using **Spring Boot**, **Thymeleaf**, **HTML/CSS**, and **JavaScript**, featuring a **Recommendation System** that provides personalized suggestions to users based on their fruit and vegetable purchases.

---

## 📌 Features

- 🔐 User Registration & Login
- 🧠 Personalized Recommendation Engine
- 🖼️ Dynamic Frontend with Thymeleaf Templates
- 📊 Admin Dashboard
- 🎨 Responsive UI using HTML, CSS, and JS
- Payment Gateway

---

## 🛠️ Technologies Used

| Layer         | Technology             |
|---------------|------------------------|
| Backend       | Spring Boot (Java)     |
| Frontend      | Thymeleaf, HTML, JS    |
| Database      | MySQL                  |
| Recommendation|  Python/FlaskAPI       |
| Build Tool    | Maven                  |
|Payment Gateway| E-sewa                 |

---

## Configure application
Edit 'application.properties' file
spring.datasource.url=jdbc:mysql://localhost:3306/db_name
spring.datasource.username=root
spring.datasource.password=yourpassword

---

## Configure Firebase
  Go to Firebase Console
  
  Create a new project or open an existing one
  
  Enable Phone Authentication under Authentication → Sign-in method
  
  Add a web app in the project settings to get your Firebase config values

  add phoneVerify.html page in templates with following content


    <!-- Firebase SDK -->
    <script src="https://www.gstatic.com/firebasejs/9.6.1/firebase-app.js"></script>
    <script src="https://www.gstatic.com/firebasejs/9.6.1/firebase-auth.js"></script>
    
    <!--add this div and script inside section-->
    
    <div class="w-100 custom-black" style="max-width: 420px;">
        <h2 class="text-center mb-4">Register User</h2>
        <div id="messageContainer" style="display: none; margin-bottom: 15px; padding: 12px; border-radius: 8px; text-align: center; font-weight: 500; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"></div>
        <form id="otpForm" class="bg-white p-4 rounded-4 shadow">
            <input type="text" id="inputPhone" class="form-control mb-3" placeholder="9XXXXXXXXX" maxlength="10" required pattern="9[0-9]{9}" autofocus>

            <div id="recaptcha-container" class="mb-3"></div>

            <button type="button" class="btn btn-success w-100 mb-3" id="phoneloginbtn">
                <i class="fas fa-paper-plane"></i> Send OTP
            </button>

            <input type="text" id="inputOtp" class="form-control mb-3" placeholder="Enter OTP">

            <button type="button" class="btn btn-dark w-100" id="verifyotp">
                <i class="fas fa-check-circle"></i> Verify OTP
            </button>
        </form>
    </div>

    <script>
        // Firebase config
        const firebaseConfig = {
            apiKey: "AIzaSyD3RuWw5PXf31TEUpAlk8VnFW61ZB38cUk",
            authDomain: "vfes-8ffb8.firebaseapp.com",
            projectId: "vfes-8ffb8",
            storageBucket: "vfes-8ffb8.appspot.com",
            messagingSenderId: "753134002598",
            appId: "1:753134002598:web:a7f88c6de3f32b952e6f67",
            measurementId: "G-R4LKK98QD4"
        };

        firebase.initializeApp(firebaseConfig);

        const loginBtn = document.getElementById("phoneloginbtn");
        const phoneInput = document.getElementById("inputPhone");
        const otpInput = document.getElementById("inputOtp");
        const verifyBtn = document.getElementById("verifyotp");
        let confirmationResult;

        // Send OTP
        loginBtn.onclick = function () {
            const rawNumber = phoneInput.value.trim();

            if (!/^9\d{9}$/.test(rawNumber)) {
                alert("Enter a valid 10-digit Nepali number starting with 98");
                return;
            }

            const fullPhone = "+977" + rawNumber;

            window.recaptchaVerifier = new firebase.auth.RecaptchaVerifier('recaptcha-container', {
                size: 'normal',
                callback: function () {},
                'expired-callback': function () {
                    alert("reCAPTCHA expired. Try again.");
                }
            });

            window.recaptchaVerifier.render();

            firebase.auth().signInWithPhoneNumber(fullPhone, window.recaptchaVerifier)
                .then(function (result) {
                    confirmationResult = result;
                    alert("OTP sent to " + fullPhone);
                })
                .catch(function (error) {
                    alert("Error sending OTP: " + error.message);
                });
        };

        // Verify OTP
        verifyBtn.onclick = function () {
            const code = otpInput.value.trim();

            if (!/^\d{6}$/.test(code)) {
                alert("Enter a valid 6-digit OTP");
                return;
            }

            confirmationResult.confirm(code)
                .then(function (result) {
                    const verifiedNumber = result.user.phoneNumber; // +9779XXXXXXXXX
                    const mobile10 = verifiedNumber.replace('+977', '');

                    // Send verified number to backend
                    fetch('/otp-success', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ mobile: mobile10 })
                    }).then(() => {
                        document.cookie = "JSESSIONID=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
                        window.location.href = "/register";
                    });
                })
                .catch(function (error) {
                    alert("Invalid OTP");
                    console.log(error);
                });
        };
    </script>


---
