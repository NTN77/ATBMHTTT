<%@ page import="model.bean.User" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Currency" %>
<%@ page import="java.text.NumberFormat" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Locale locale = new Locale("vi", "VN");
    Currency currency = Currency.getInstance(locale);
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
    numberFormat.setCurrency(currency);
%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"> <!--icon-->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"
            integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <title>Thông tin tài khoản</title>
    <style>
        .my-custom-scrollbar {
            position: relative;
            height: 50%;
            overflow: auto;
        }

        .table-wrapper-scroll-y {
            display: block;
        }

        .dt-empty, .dt-info {
            display: none;
        }

        table {
            border-collapse: collapse;
        }

        th {
            background-color: green;
            Color: white;
        }

        th, td {
            width: 150px;
            text-align: center;
            border: 1px solid black;
            padding: 5px

        }

        .geeks {
            border: hidden;
            outline: none;
        }

        .gfg {
            border-collapse: separate;
            border-spacing: 0 35px;
        }

        span {
            font-weight: bold;
            color: black;
        }

        #detailOrder {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            overflow: auto;
            z-index: 9999;
            background-color: #e0eaf4; /* Adjust the background color as needed */
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.5); /* Optional: Add a box shadow for a subtle effect */
        }

        .title-information {
            text-align: center;
        }

        .expand_key_area {
            background-color: #e9ecef;
            justify-content: space-between;
            border-radius: 5px;
            padding: 6px 12px;
        }

        /* CSS cho Tab */
        .nav-pills .nav-link {
            cursor: pointer;
            font-weight: bold;
            border: 1px solid #eeeeee;
            border-radius: 0px;
        }

        /* Cách hiển thị tab nội dung */
        .tab-content {
            padding: 20px;
            border: 1px solid #eeeeee;
        }

        .hr-information {
            border: 0;
            border-top: 2px dashed #000000;
        }


        /**
        Hiện thực khoá.
         */
        .auth-key {
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 15px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .auth-key-header {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
        }

        .auth-key-header .img-key {
            width: 20px;
            height: 20px;
            margin-right: 10px;
        }

        .auth-key-details {
            margin-bottom: 10px;
            color: #555;
        }

        .auth-key-details span {
            display: block;
            font-weight: normal;
        }

        .auth-key-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .auth-key-actions .status {
            color: green;
            font-weight: normal;
            font-style: italic;
        }

        .auth-key-actions button {
            background-color: #e74c3c;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 5px;
            cursor: pointer;
        }

        .auth-key-actions button:hover {
            background-color: #c0392b;
        }

        /*    Màn hình thêm khoá mới*/

        .overlay {
            display: none; /* Ẩn mặc định */
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5); /* Màu đen với độ mờ */
            z-index: 10; /* Hiển thị trên mọi thành phần khác */
        }


        #add-new-key {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%); /* Căn giữa */
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0px 5px 15px rgba(0, 0, 0, 0.2);
            z-index: 11; /* Trên overlay */
            width: 50%; /* Chiều rộng của box */
            display: none; /* Ẩn mặc định */
        }

        #add-new-key h2 {
            text-align: center;

        }

        #add-new-key textarea {
            background-color: #e9ecef;
            resize: none;
        }

        #add-new-key .download-icon {
            cursor: pointer;
            color: red;
            font-size: 18px;
            margin-left: 5px;
        }

        #add-new-key .link {
            font-size: 14px;
            font-style: italic;
            text-decoration: underline;
            cursor: pointer;
            color: blue;
        }

        #add-new-key .close-screen-key {
            position: absolute;
            top: 10px;
            right: 10px;
            cursor: pointer;
            font-size: 20px;
            color: #333;
        }

        #up-new-key {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%); /* Căn giữa */
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0px 5px 15px rgba(0, 0, 0, 0.2);
            z-index: 11; /* Trên overlay */
            width: 50%; /* Chiều rộng của box */
            display: none; /* Ẩn mặc định */
        }

        #up-new-key h2 {
            text-align: center;

        }

        #up-new-key textarea {
            background-color: #e9ecef;
            resize: none;
        }

        #up-new-key .close-screen-key {
            position: absolute;
            top: 10px;
            right: 10px;
            cursor: pointer;
            font-size: 15px;
            background-color: #f27474;
        }


    </style>
</head>
<%
    User user = (User) (request.getSession().getAttribute("auth"));
    if (user != null) {
%>
<body>
<!-- Tab Thiết lập bảo mật -->
<div class="tab-pane fade show active" id="security" role="tabpanel" aria-labelledby="security-tab">
    <div class="header-security d-flex justify-content-between">
        <h3>Thiết lập bảo mật</h3>
        <a class="btn btn-warning" href="<%=request.getContextPath()%>/views/ReportPKey/ReportKey.jsp">Báo mất khoá</a>
    </div>
    <hr class="hr-information">
    <div class="d-flex align-items-center expand_key_area">
        <span>Khoá mã hoá công khai</span>
        <button onclick="verifyPwd()" class="btn btn-outline-danger ms-2 position-relative"
                id="expand_pubKey">
            <i class="fa-solid fa-eye"></i>
        </button>
    </div>
    <div id="authKeysContainer">
    </div>

    <%--                            <div class="auth-key mt-2">--%>
    <%--                                <div class="auth-key-header">--%>
    <%--                                    <i class="fa-solid fa-key img-key"></i>--%>
    <%--                                    <strong>lenovo-thinkbook-g7</strong>--%>
    <%--                                </div>--%>
    <%--                                <div class="auth-key-details">--%>
    <%--                                    <span>SHA256: fVoTv61qlwUodrkrMuWbIFouXjjbSYE7z0vf6sRXDAg</span>--%>
    <%--                                    <span>Đã được thêm vào 1-12-2024</span>--%>
    <%--                                </div>--%>
    <%--                                <div class="auth-key-actions">--%>
    <%--                                    <span class="status">Lần cuối được sử dụng là 5 ngày trước </span>--%>
    <%--                                    <button>Xoá</button>--%>
    <%--                                </div>--%>
    <%--                            </div>--%>
    <%--                            <div class="auth-key mt-2">--%>
    <%--                                <div class="auth-key-header">--%>
    <%--                                    <i class="fa-solid fa-key img-key"></i>--%>
    <%--                                    <strong>lenovo-thinkbook-g7</strong>--%>
    <%--                                </div>--%>
    <%--                                <div class="auth-key-details">--%>
    <%--                                    <span>SHA256: fVoTv61qlwUodrkrMuWbIFouXjjbSYE7z0vf6sRXDAg</span>--%>
    <%--                                    <span>Đã được thêm vào 1-12-2024</span>--%>
    <%--                                </div>--%>
    <%--                                <div class="auth-key-actions">--%>
    <%--                                    <span class="status">Lần cuối được sử dụng là 5 ngày trước </span>--%>
    <%--                                    <button>Xoá</button>--%>
    <%--                                </div>--%>
    <%--                            </div>--%>

    <div class="button-security d-flex justify-content-center mt-2">
        <button class="btn btn-info" onclick="showAddKeyScreen()">Thêm khoá mới</button>
        <button class="btn btn-info ms-2" onclick="showUpKeyScreen()">Tải khoá có sẵn</button>
    </div>

</div>
<div class="mt-4 " id="add-new-key">
    <h2 class="mb-4">Tạo khoá mới</h2>

    <button onclick="exitAddKey()" class="btn-close close-screen-key">

    </button>

    <!-- Nhập tên khoá -->
    <div class="mb-3">
        <label for="keyName" class="form-label fw-bold">Tên khoá</label>
        <input type="text" id="keyName" class="form-control"
               placeholder="Vui lòng sử dụng - thay cho khoảng trắng. Ví dụ: lenovo-thinkbook-g7">
    </div>
    <button onclick="generateKey()" class="btn btn-warning add-btn mb-2">Tạo khoá</button>
    <br>
    <p id="generateCountdownText" style="display: none; font-style: italic; font-size: 12px"></p>
    <div class="row">
        <div class="col-md-6 mb-3">
            <label class="form-label fw-bold">Public Key</label>
            <textarea id="publicKey" class="form-control" rows="4" readonly></textarea>
        </div>
        <div class="col-md-6 mb-3">
            <label class="form-label fw-bold">
                Private Key
                <span onclick="downloadPriKey()" class="download-icon" style="display: none" title="Tải xuống">
                        <i class="fa-solid fa-download"></i>
                    </span>
            </label>
            <textarea id="privateKey" class="form-control" rows="4" readonly></textarea>
        </div>
    </div>
    <!-- Hiển thị thời gian đếm ngược cho private key -->
    <p id="privateKeyCountdownText" style="display: none; font-style: italic; font-size: 12px"></p>


    <!-- Link và Xác nhận -->
    <div class="d-flex justify-content-between align-items-center">
        <span class="link" onclick="goBack()">Bạn đã có khoá?</span>
        <button class="btn btn-success" onclick="addKeyIntoDB()">Xác nhận</button>
    </div>
</div>

<div class="mt-4 " id="up-new-key">
    <h2 class="mb-4">Tải khoá lên</h2>

    <button onclick="exitUpKey()" class="btn-close close-screen-key">
    </button>

    <!-- Nhập tên khoá -->
    <div class="mb-3">
        <label for="ukeyName" class="form-label fw-bold">Tên khoá</label>
        <input type="text" id="ukeyName" class="form-control"
               placeholder="Vui lòng sử dụng - thay cho khoảng trắng. Ví dụ: lenovo-thinkbook-g7">
    </div>

    <div class="row">
        <div class="col-md-12 mb-16">
            <label class="form-label fw-bold">Public Key</label>
            <button onclick="uploadKey()" class="btn btn-warning add-btn mb-2">
                <i class="fa-solid fa-upload"></i> Tải lên
            </button>
            <textarea id="upublicKey" class="form-control" rows="4" readonly></textarea>
        </div>
    </div>
    <!-- Link và Xác nhận -->
    <div class="d-flex justify-content-center align-items-center mt-2">
        <button class="btn btn-success" onclick="upKeyIntoDB()">Xác nhận</button>
    </div>
</div>

<div class="overlay"></div>

<input type="hidden" id="statusNumber" value="-1">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="https://cdn.jsdelivr.net/npm/moment@2.29.1/moment.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/moment@2.29.1/locale/vi.js"></script>
<script>

    moment.locale('vi');

    <%--    Thiết lập ẩn hiện các tab:--%>
    document.querySelectorAll('.nav-link').forEach(tab => {
        tab.addEventListener('click', function () {
            // Xóa lớp 'active' của tất cả các tab
            document.querySelectorAll('.nav-link').forEach(tabItem => tabItem.classList.remove('active'));

            // Thêm lớp 'active' vào tab đang được chọn
            this.classList.add('active');

            // Ẩn tất cả nội dung tab
            document.querySelectorAll('.tab-pane').forEach(tabContent => {
                tabContent.classList.remove('show', 'active');
            });

            // Hiển thị nội dung của tab được chọn
            const targetTab = document.querySelector(this.getAttribute('href'));
            targetTab.classList.add('show', 'active');
        });
    });

    /**
     * Xuwr lys them khoa moi
     */
    function showAddKeyScreen() {
        document.getElementById('add-new-key').style.display = 'block';
        document.querySelector('.overlay').style.display = 'block';
    }

    function exitAddKey() {
        document.querySelector('.overlay').style.display = 'none';
        document.getElementById('add-new-key').style.display = 'none';
        document.getElementById("publicKey").value = "";
    }

    function showUpKeyScreen() {
        document.getElementById('up-new-key').style.display = 'block';
        document.querySelector('.overlay').style.display = 'block';
    }

    function exitUpKey() {
        document.querySelector('.overlay').style.display = 'none';
        document.getElementById('up-new-key').style.display = 'none';
        document.getElementById("upublicKey").value = "";
    }


    let cooldownTime = 30; //30s de tao khoa moi.
    let privateKeyTimeout = 60;
    let countdownInterval, privateKeyInterval;

    /**
     * Taoj khoa
     */
    function generateKey() {
        const generateButton = document.querySelector(".add-btn");
        const generateCountdownText = document.getElementById("generateCountdownText");

        if (privateKeyInterval) {
            clearInterval(privateKeyInterval);
        }

        privateKeyTimeout = 60;

        $.ajax({
            method: "GET",
            url: "/HandMadeStore/generate-key",
            success: function (resp) {
                //HIển thị pub & pri
                document.getElementById("publicKey").value = resp.publicKey;
                document.getElementById("privateKey").value = resp.privateKey;
                document.querySelector(".download-icon").style.display = "inline";


                // Bắt đầu đếm ngược cho private key (sau 60 giây sẽ bị ẩn và xóa)
                privateKeyCountdown();

                // Bắt đầu đếm ngược thời gian (30 giây) cho nút Tạo khoá
                startGenerateKeyCountdown(generateButton, generateCountdownText);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("AJAX Error: " + textStatus + " - " + errorThrown);
                alert("Không tạo được khoá, vui lòng thử lại sau");
                // Bật lại nút Tạo khoá nếu có lỗi
                generateButton.disabled = false;
            }
        });
    }


    // count down cho tao khoa.
    function startGenerateKeyCountdown(generateButton, generateCountdownText) {

        countdownInterval = setInterval(function () {
            cooldownTime--;
            generateCountdownText.style.display = "inline";
            generateButton.disabled = true;
            generateCountdownText.textContent = `Vui lòng chờ ${cooldownTime} giây để tạo lại khoá.`;

            if (cooldownTime <= 0) {
                clearInterval(countdownInterval);
                generateCountdownText.style.display = "none";
                generateButton.disabled = false;
                cooldownTime = 30;
            }
        }, 1000);
    }

    function privateKeyCountdown() {
        privateKeyInterval = setInterval(function () {
            privateKeyTimeout--;

            const privateKeyCountdownText = document.getElementById("privateKeyCountdownText");
            privateKeyCountdownText.style.display = "inline";
            privateKeyCountdownText.textContent = `Private key sẽ hết hạn trong ${privateKeyTimeout} giây.`;

            if (privateKeyTimeout <= 0) {
                clearInterval(privateKeyInterval);
                document.getElementById("privateKey").value = "";
                document.querySelector(".download-icon").style.display = "none";
                privateKeyCountdownText.style.display = "none";
            }
        }, 1000);
    }

    // download private key về máy tính.
    function downloadPriKey() {
        const privateKey = document.getElementById("privateKey").value;
        if (privateKey) {
            const blob = new Blob([privateKey], {type: "application/x-pem-file"});
            const link = document.createElement("a");
            link.href = URL.createObjectURL(blob);
            link.download = "private_key.pem";
            link.click();
        } else {
            alert("Lỗi không tải xuống!")
        }
    }

    /**
     * Đẩy khoá public lên database.
     *
     */
    function addKeyIntoDB() {
        const title = document.getElementById("keyName").value;
        const publicKey = document.getElementById("publicKey").value;

        $.ajax({
            method: "POST",
            url: "/HandMadeStore/add-public-key",
            data: {
                title: title,
                publicKey: publicKey,
                userId: <%=user.getId()%>
            },
            success: function (response) {
                if (response.success) {
                    Swal.fire({
                        position: "center",
                        icon: "success",
                        title: "Thành công",
                        text: response.message,
                        showConfirmButton: true,
                        confirmButtonText: "OK"
                    }).then (() => {
                        window.location.reload();
                    });
                } else {
                    Swal.fire({
                        position: "center",
                        icon: "error",
                        title: "Thất bại",
                        text: response.message,
                        showConfirmButton: true,
                        confirmButtonText: "OK"
                    });
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("AJAX Error: " + textStatus + " - " + errorThrown);
                Swal.fire({
                    position: "center",
                    icon: "error",
                    title: "Lỗi",
                    text: "Không thể thêm khoá vào cơ sở dữ liệu, vui lòng thử lại sau",
                    showConfirmButton: true,
                    confirmButtonText: "OK"
                });
            }
        });
    }


    // Upload public key lên giao diện.
    function uploadKey() {
        const inputFile = document.createElement('input');
        inputFile.type = 'file';
        inputFile.accept = '.pem';

        inputFile.onchange = (event) => {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                // đọc ội dung file.
                reader.onload = (e) => {
                    const content = e.target.result;

                    document.getElementById('upublicKey').value = content.trim();
                };

                reader.onerror = () => {
                    Swal.fire({
                        position: "center",
                        icon: "error",
                        title: "Lỗi",
                        text: "Lỗi khi đọc tệp. Vui lòng thử lại",
                        showConfirmButton: true,
                        confirmButtonText: "OK"
                    });

                };

                reader.readAsText(file);


            }
        };
        inputFile.click();
    }

    //public key must be format pem.
    function validatePublicKey(publicKey) {
        const pemRex = /-----BEGIN PUBLIC KEY-----\n([A-Za-z0-9+/=\n]+)-----END PUBLIC KEY-----/;
        if (!pemRex.test(publicKey.trim())) {
            return false;
        }
        return true;
    }

    function upKeyIntoDB() {
        const title = document.getElementById("ukeyName").value;
        const publicKey = document.getElementById("upublicKey").value;

        // Kiểm tra định dạng Public Key
        if (!validatePublicKey(publicKey)) {
            Swal.fire({
                position: "center",
                icon: "error",
                title: "Lỗi",
                text: "Public key không đúng định dạng PEM!",
                showConfirmButton: true,
                confirmButtonText: "OK"
            });
            return;
        }

        // Gửi AJAX request
        $.ajax({
            method: "POST",
            url: "/HandMadeStore/add-public-key",
            data: {
                title: title,
                publicKey: publicKey,
                userId: <%=user.getId()%>
            },
            success: function (response) {
                if (response.success) {
                    Swal.fire({
                        position: "center",
                        icon: "success",
                        title: "Thành công",
                        text: response.message,
                        showConfirmButton: true,
                        confirmButtonText: "OK"
                    }).then(() => {
                        window.location.reload();
                    });
                } else {
                    Swal.fire({
                        position: "center",
                        icon: "error",
                        title: "Thất bại",
                        text: response.message,
                        showConfirmButton: true,
                        confirmButtonText: "OK"
                    });
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("AJAX Error: " + textStatus + " - " + errorThrown);
                Swal.fire({
                    position: "center",
                    icon: "error",
                    title: "Lỗi",
                    text: "Không thể thêm khoá vào cơ sở dữ liệu, vui lòng thử lại sau",
                    showConfirmButton: true,
                    confirmButtonText: "OK"
                });
            }
        });
    }



    /** Xử lý nút xem khoá */
    function verifyPwd() {
        swal.fire({
            title: "Nhập mật khẩu để tiếp tục",
            input: "password",
            inputAttributes: {
                autocapitalize: "off"
            },
            showCancelButton: true,
            confirmButtonText: "Gửi",
            cancelButtonText: "Huỷ",
            showLoaderOnConfirm: true,
            preConfirm: (enteredPassword) => {
                return $.ajax({
                    method: "POST",
                    url: "/HandMadeStore/changeUserInfo",
                    data: {
                        action: "verifyPwd",
                        password: enteredPassword
                    }
                }).then((response) => {
                    // Nếu server trả về chuỗi
                    if (response === "Mật khẩu chính xác!") {
                        return true; // Xác nhận thành công
                    } else {
                        Swal.showValidationMessage(response); // Hiển thị thông báo lỗi từ server
                        return false; // Không xác nhận
                    }
                }).catch((error) => {
                    Swal.showValidationMessage("Lỗi khi kết nối tới server!"); // Lỗi kết nối hoặc server
                    console.error(error);
                });
            },
            allowOutsideClick: () => !Swal.isLoading()
        }).then((result) => {
            if (result.isConfirmed) {
                Swal.fire('Mật khẩu chính xác!', '', 'success');
                fetchPublicKeys();
            }
        });
    }

    // Sau khi verify password thành công, thì sẽ hiển thị public key lấy từ database.
    function fetchPublicKeys() {
        const userId = <%=user.getId()%>;

        $.ajax({
            url: '/HandMadeStore/get-public-keys?userId=' + userId,
            method: 'GET',
            success: function (response) {
                if (response.success !== false) {
                    // Xử lý hiển thị các khoá công khai
                    displayKeys(response);
                } else {
                    Swal.fire("Lỗi", response.message, "error");
                }
            },
            error: function (xhr, status, error) {
                Swal.fire("Lỗi", "Không thể tải thông tin khoá!", "error");
            }
        });
    }

    function displayKeys(keys) {
        const keysContainer = document.getElementById("authKeysContainer");
        keysContainer.innerHTML = '';

        // Duyệt qua danh sách các khoá và hiển thị chúng
        keys.forEach(key => {
            const keyElement = document.createElement("div");
            keyElement.classList.add("auth-key", "mt-2");


            // Format thời gian với moment.js
            const createdTime = moment(key.createdTime).locale('vi').format('DD/MM/YYYY [lúc] HH:mm:ss');

            let updatedTime;

            // Kiểm tra nếu updatedTime là null
            if (key.updatedTime) {
                updatedTime = moment(key.updatedTime).locale('vi').format('DD/MM/YYYY [lúc] HH:mm:ss');
            } else {
                updatedTime = "chưa sử dụng";
            }

            keyElement.innerHTML = `
            <div id="auth-key-${key.id}">
                <div class="auth-key-header">
                    <i class="fa-solid fa-key img-key"></i>
                    <strong>${key.title}</strong>
                </div>
                <div class="auth-key-details">
                    <span>Nội dung khoá : <br> ${key.publicKey.slice(0, 30)}...${key.publicKey.slice(-30)}</span>
                    <span>Đã được thêm vào ${createdTime}</span>
                </div>
                <div class="auth-key-actions">
                    <span class="status">Lần cuối được sử dụng là ${updatedTime}</span>
                   <button onclick="deleteKey(${key.id}, 'auth-key-${key.id}')">Xoá</button>
                </div>
            </div>
        `;

            keysContainer.appendChild(keyElement);
        });
    }

    function deleteKey(publicKeyId, elementId) {
        Swal.fire({
            title: "Bạn có chắc muốn xóa không?",
            text: "Thao tác này không thể hoàn tác!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            cancelButtonColor: "#3085d6",
            confirmButtonText: "Xóa",
            cancelButtonText: "Hủy"
        }).then((result) => {
            if (result.isConfirmed) {
                // Gửi yêu cầu xóa nếu người dùng xác nhận
                $.ajax({
                    method: "POST",
                    url: "/HandMadeStore/remove-public-key-ajax-handle",
                    data: {
                        action: "removePublicKey",
                        publicKeyId: publicKeyId
                    },
                    success: function (response) {
                        Swal.fire({
                            position: "center",
                            icon: "success",
                            title: "Xóa khóa công khai thành công",
                            showConfirmButton: false,
                            timer: 1000
                        });

                        // Xóa phần tử HTML có id là elementId
                        const element = document.getElementById(elementId);
                        if (element) {
                            element.remove();
                        }
                    },
                    error: function () {
                        Swal.fire({
                            title: "Lỗi!",
                            text: "Không thể xóa khóa công khai.",
                            icon: "error",
                            confirmButtonText: "OK"
                        });
                    }
                });
            }
        });
    }

</script>
</body>
<%
    } else {
        response
                .
                sendRedirect
                        (
                                request
                                        .
                                        getContextPath
                                                (
                                                )
                                        +
                                        "/login"
                        )
        ;
    }
%>
</html>