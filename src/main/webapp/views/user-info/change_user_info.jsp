<%@ page import="model.bean.User" %>
<%@ page import="model.bean.Order" %>
<%@ page import="java.util.List" %>
<%@ page import="model.bean.OrderDetail" %>
<%@ page import="model.bean.Product" %>
<%@ page import="model.service.*" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Currency" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="org.eclipse.tags.shaded.org.apache.xpath.operations.Or" %>
<%@ page import="utils.HashPassword" %>
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
<div class="tab-content" id="profileTabContent">
    <!-- Tab Thông tin cá nhân -->
    <div class="tab-pane fade show active" id="info" role="tabpanel" aria-labelledby="info-tab">
        <h3>Thông tin cá nhân</h3>
        <hr class="hr-information">
        <div class="mb-2">
            <div class="row input-group d-flex justify-content-center">
                <div class="col-12 col-md-5  left-info">
                    <div class="text-warning fst-italic text-center" id="notifyChanged"></div>
                    <div id="toggleInforBox">
                        <label for="input_name">Tên tài khoản: </label>
                        <input type="text" class="form-control" disabled id="input_name"
                               name="input_name" value="<%=user.getName()%>">
                        <div class="input-group-append d-flex">
                            <button class="btn btn-outline-warning" type="button"
                                    onclick="editName()" id="edit_name">Thay đổi
                            </button>
                            <button class="btn btn-outline-warning" type="button"
                                    onclick="cancelName()" style="display: none" id="cancel_name">
                                Hủy bỏ
                            </button>
                            <button class="btn btn-outline-success" type="button"
                                    onclick="changeName()" style="display: none" id="save_name">Lưu
                                thay đổi
                            </button>
                        </div>
                    </div>
                    <div>
                        <label for="input_phone">Số điện thoại: </label>
                        <input type="text" class="form-control" disabled id="input_phone"
                               name="input_phone" value="<%=user.getPhoneNumber()%>">
                        <div class="input-group-append d-flex">
                            <button class="btn btn-outline-warning" type="button"
                                    onclick="editPhone()" id="edit_phone">Thay đổi
                            </button>
                            <button class="btn btn-outline-warning" type="button"
                                    onclick="cancelPhone()" style="display: none" id="cancel_phone">
                                Hủy bỏ
                            </button>
                            <button class="btn btn-outline-success" type="button"
                                    onclick="changePhone()" style="display: none" id="save_phone">
                                Lưu thay đổi
                            </button>
                        </div>
                    </div>
                    <div>
                        <label for="input_email">Email: </label>
                        <input type="text" class="form-control" disabled id="input_email"
                               name="input_email" value="<%=user.getEmail()%>">
                    </div>
                    <div class="my-1 text-end">
                        <a class="text-decoration-none"
                           href="<%=request.getContextPath()%>/forgotpassword">Đặt lại mật khẩu?</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<input type="hidden" id="statusNumber" value="-1">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="https://cdn.jsdelivr.net/npm/moment@2.29.1/moment.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/moment@2.29.1/locale/vi.js"></script>
<script>
    // XỬ LÝ DÀNH CHO THAY ĐỔI THÔNG TIN CÁ NHÂN.
    function changeName() {
        const newName = document.getElementById("input_name").value;
        $.ajax({
            method: "POST",
            url: "/HandMadeStore/changeUserInfo",
            data: {
                action: "changeName",
                newName: newName
            },
            success: function (response) {
                document.getElementById("notifyChanged").innerHTML = response;
                cancelName();
            },
            error: function () {
                console.log("Thay đổi thất bại!");
            }
        })
    }

    function changePhone() {
        const newPhone = document.getElementById("input_phone").value;
        $.ajax({
            method: "POST",
            url: "/HandMadeStore/changeUserInfo",
            data: {
                action: "changePhone",
                newPhone: newPhone
            },
            success: function (response) {
                document.getElementById("notifyChanged").innerHTML = response;
                cancelPhone();
            },
            error: function () {
                console.log("Thay đổi thất bại!");
            }
        })
    }

    function editName() {
        document.getElementById("input_name").disabled = false;
        document.getElementById('save_name').style.display = 'inline';
        document.getElementById('cancel_name').style.display = 'inline';
        document.getElementById('edit_name').style.display = 'none';
    }

    function cancelName() {
        document.getElementById("input_name").disabled = true;
        document.getElementById('save_name').style.display = 'none';
        document.getElementById('cancel_name').style.display = 'none';
        document.getElementById('edit_name').style.display = 'inline';
    }

    function editPhone() {
        document.getElementById("input_phone").disabled = false;
        document.getElementById('save_phone').style.display = 'inline';
        document.getElementById('cancel_phone').style.display = 'inline';
        document.getElementById('edit_phone').style.display = 'none';
    }

    function cancelPhone() {
        document.getElementById("input_phone").disabled = true;
        document.getElementById('save_phone').style.display = 'none';
        document.getElementById('cancel_phone').style.display = 'none';
        document.getElementById('edit_phone').style.display = 'inline';
    }

    function goBack() {
        const previousPage = document.referrer;
        if (previousPage) {
            window.location.href = previousPage;
        } else {
            window.history.href = "/";
        }
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