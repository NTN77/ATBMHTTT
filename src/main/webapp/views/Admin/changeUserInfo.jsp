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


        #add-new-key  {
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

        #up-new-key  {
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
<%
    List<Order> orders = OrderService.getInstance().getOrderByCustomerId(user.getId() + "", 3, 0);
    int allOrderNumber = OrderService.getInstance().ordersNumber(user.getId() + "");
    int showCouter = 0;
    int showLimit = allOrderNumber > 3 ? 3 : orders.size();
    int all = OrderService.getInstance().ordersNumber(user.getId() + "");
    int s0 = OrderService.getInstance().ordersNumber(user.getId() + "", 0);
    int s1 = OrderService.getInstance().ordersNumber(user.getId() + "", 1);
    int s2 = OrderService.getInstance().ordersNumber(user.getId() + "", 2);
    int s3 = OrderService.getInstance().ordersNumber(user.getId() + "", 3);
    int s4 = OrderService.getInstance().ordersNumber(user.getId() + "", 4);
%>
<body>


<div class="container p-4 rounded">
    <div class="fw-bold text-start" style="font-size: 30px; color: #0dcaf0">
        <div>
            <i onclick="goBack()"
               class="fa-solid fa-arrow-left" style="color: #183153; cursor: pointer"></i>
        </div>
    </div>
    <div>
        <h3 class="title-information">Chào bạn, <%=user.getName()%> !</h3>

        <div class="container mt-4">
            <div class="row">
                <!-- Sidebar (Tab dọc) -->
                <div class="col-12 col-md-3">
                    <div class="nav flex-column nav-pills" id="profileTab" role="tablist" aria-orientation="vertical">
                        <a class="nav-link active" id="info-tab" data-bs-toggle="pill" href="#info" role="tab"
                           aria-controls="info" aria-selected="true">Thông tin cá nhân</a>
                        <a class="nav-link" id="security-tab" data-bs-toggle="pill" href="#security" role="tab"
                           aria-controls="security" aria-selected="false">Thiết lập bảo mật</a>
                        <a class="nav-link" id="order-history-tab" data-bs-toggle="pill" href="#order-history"
                           role="tab" aria-controls="order-history" aria-selected="false">Lịch sử mua hàng</a>
                    </div>
                </div>

                <!-- Nội dung của các tab -->
                <div class="col-12 col-md-9">
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

                        <!-- Tab Thiết lập bảo mật -->
                        <div class="tab-pane fade" id="security" role="tabpanel" aria-labelledby="security-tab">
                            <div class="header-security d-flex justify-content-between">
                                <h3>Thiết lập bảo mật</h3>
                                <button class="btn btn-warning">Báo mất khoá</button>
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
                            <div class="button-security d-flex justify-content-center mt-2">
                                <button class="btn btn-info" onclick="showAddKeyScreen()">Thêm khoá mới</button>
                                <button class="btn btn-info ms-2" onclick="showUpKeyScreen()">Tải khoá có sẵn</button>
                            </div>

                        </div>
                        <!-- Tab Lịch sử mua hàng -->
                            <div class="tab-pane fade" id="order-history" role="tabpanel"
                             aria-labelledby="order-history-tab">
                            <h3>Lịch sử đơn hàng</h3>
                            <hr class="hr-information">
                            <!-- Đoạn mã lịch sử đơn hàng của bạn -->
                            <div class="btn-group mr-2 mb-2" role="group" aria-label="First group">
                                <button type="button" class="btn btn-info"
                                        onclick="orderFilter(<%=user.getId()%>, <%=3%>)">
                                    Tất cả
                                    <span><%=all > 0 ? "(" + all + ")" : ""%></span>

                                </button>
                                <button type="button" class="btn btn-warning"
                                        onclick="orderFilter(<%=user.getId()%>, <%=3%>,0)">Chờ xác nhận
                                    <span><%=s0 > 0 ? "(" + s0 + ")" : ""%></span>
                                </button>
                                <button type="button" class="btn btn-secondary" style="background-color: cadetblue"
                                        onclick="orderFilter(<%=user.getId()%>, <%=3%>,1)">Đang đóng gói
                                    <span><%=s1 > 0 ? "(" + s1 + ")" : ""%></span>
                                </button>
                                <button type="button" class="btn btn-primary"
                                        onclick="orderFilter(<%=user.getId()%>, <%=3%>,2)">Đang giao
                                    <span><%=s2 > 0 ? "(" + s2 + ")" : ""%></span>

                                </button>
                                <button type="button" class="btn btn-success"
                                        onclick="orderFilter(<%=user.getId()%>, <%=3%>,3)">Thành công
                                    <span><%=s3 > 0 ? "(" + s3 + ")" : ""%></span>
                                </button>
                                <button type="button" class="btn btn-danger"
                                        onclick="orderFilter(<%=user.getId()%>, <%=3%>,4)">Đã hủy
                                    <span><%=s4 > 0 ? "(" + s4 + ")" : ""%></span>
                                </button>
                            </div>
                            <!-- Table lịch sử đơn hàng -->
                            <table id="data" class="table table-light gfg">
                                <thead>
                                <tr class="text-center sticky-top">
                                    <th class="text-nowrap">Sản phẩm</th>
                                    <th class="text-nowrap">Địa Chỉ Giao</th>
                                    <th class="text-nowrap">Ngày Đặt Hàng</th>
                                    <th class="text-nowrap">Tổng Tiền Hóa Đơn</th>
                                    <th class="text-nowrap">Trạng thái</th>
                                </tr>
                                </thead>
                                <tbody>
                                <%
                                    for (int i = 0; i < showLimit; i++) {
                                        Order o = orders.get(i);
                                        if (o != null) {
                                            showCouter += 1;
                                            User customer = UserService.getInstance().getUserById(o.getUserId() + "");
                                %>
                                <tr class="text-center" style=" cursor: pointer"
                                    onclick="showOrderDetails(event,this)"
                                    id="<%=o.getId()%>"
                                >
                                    <td class="geeks" style="border: 1px solid #c9c9c9;">
                                        <%!Product p;%>
                                        <%
                                            for (OrderDetail orderDetail : OrderService.getInstance().getOrderDetailsByOrderId(o.getId()
                                                    + "")) {
                                                p = ProductService.getInstance().getProductById(orderDetail.getProductId() + "");
                                        %>
                                        <div class="d-flex mb-2">
                                            <div>
                                                <img src="<%=request.getContextPath()+"/"+ ImageService.pathImageOnly(p.getId())%>"
                                                     alt="" width="100px">
                                            </div>
                                            <div style="width: 150px; display:flex; align-items: center">
                                                <%=p.getName()%>
                                            </div>
                                        </div>
                                        <%}%>
                                    </td>
                                    <td class="geeks "
                                        style="border-top: 1px solid #c9c9c9;border-bottom: 1px solid #c9c9c9; vertical-align: middle"><%=o.getAddress()%>
                                    </td>
                                    <td class="geeks"
                                        style="border-top: 1px solid #c9c9c9;border-bottom: 1px solid #c9c9c9; vertical-align: middle;"><%=o.getOrderDate()%>
                                    </td>
                                    <td class="geeks"
                                        style="border-top: 1px solid #c9c9c9;border-bottom: 1px solid #c9c9c9; vertical-align: middle;"><%=numberFormat.format(o.getTotalPrice())%>
                                    </td>
                                    <td class="geeks"
                                            <%!
                                                String backgroundColor = "";
                                                String sttvalue = "";
                                            %>
                                            <%
                                                if (o.isPreparing()) {
                                                    backgroundColor = "#5F9EA0";
                                                    sttvalue = "Đang đóng gói";
                                                } else if (o.isDeliveringOrder()) {
                                                    backgroundColor = "#0171d3";
                                                    sttvalue = "Đang giao";
                                                } else if (o.isWaitConfirmOrder()) {
                                                    backgroundColor = "#ffcc00";
                                                    sttvalue = "Chờ xác nhận";
                                                } else if (o.isCanceledOrder()) {
                                                    backgroundColor = "#ff0000";
                                                    sttvalue = "Đã hủy";
                                                } else if (o.isSucccessfulOrder()) {
                                                    backgroundColor = "#4d8a54";
                                                    sttvalue = "Thành công";
                                                }%>
                                        style="background-color: <%=backgroundColor%>; color: #ffffff;vertical-align: middle;"
                                    ><%=sttvalue%>
                                    </td>
                                </tr>
                                <%
                                        }
                                    }
                                %>
                                </tbody>
                            </table>
                            <%if (showCouter < allOrderNumber) {%>
                            <div class="d-flex justify-content-center" id="morebox">
                <span id="more" class="fst-italic fw-bold" style="cursor: pointer"
                      onclick="more(<%=user.getId()%>, <%=3%>)"
                >Xem thêm...
                </span>
                            </div>
                            <%}%>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div id="detailOrder" style="display:none;">
    </div>
    <div class="mt-4 " id="add-new-key">
        <h2 class="mb-4">Tạo khoá mới</h2>

        <button onclick="exitAddKey()" class="btn-close close-screen-key">

        </button>

        <!-- Nhập tên khoá -->
        <div class="mb-3">
            <label for="keyName" class="form-label fw-bold">Tên khoá</label>
            <input type="text" id="keyName" class="form-control" placeholder="Vui lòng sử dụng - thay cho khoảng trắng. Ví dụ: lenovo-thinkbook-g7">
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
            <input type="text" id="ukeyName" class="form-control" placeholder="Vui lòng sử dụng - thay cho khoảng trắng. Ví dụ: lenovo-thinkbook-g7">
        </div>

        <div class="row">
            <div class="col-md-12 mb-16">
                <label class="form-label fw-bold">Public Key</label>
                <button onclick="uploadKey()" class="btn btn-warning add-btn mb-2">
                <i class="fa-solid fa-upload"></i> Tải lên</button>
                <textarea id="upublicKey" class="form-control" rows="4" readonly></textarea>
            </div>
        </div>
        <!-- Link và Xác nhận -->
        <div class="d-flex justify-content-center align-items-center mt-2">
            <button class="btn btn-success" onclick="upKeyIntoDB()">Xác nhận</button>
        </div>
    </div>

    <div class="overlay"></div>
</div>

<input type="hidden" id="allOrderNumber" value="<%=allOrderNumber%>">
<input type="hidden" id="showCouter" value="<%=showCouter%>">
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
            error: function(jqXHR, textStatus, errorThrown) {
                console.error("AJAX Error: " + textStatus + " - " + errorThrown);
                alert("Không tạo được khoá, vui lòng thử lại sau");
                // Bật lại nút Tạo khoá nếu có lỗi
                generateButton.disabled = false;
            }
        });
    }


    // count down cho tao khoa.
    function startGenerateKeyCountdown(generateButton, generateCountdownText) {

        countdownInterval = setInterval(function() {
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
        privateKeyInterval = setInterval(function() {
            privateKeyTimeout--;

            const privateKeyCountdownText = document.getElementById("privateKeyCountdownText");
            privateKeyCountdownText.style.display = "inline";
            privateKeyCountdownText.textContent = `Private key sẽ hết hạn trong ${privateKeyTimeout} giây.`;

            if (privateKeyTimeout <= 0) {
                clearInterval(privateKeyInterval);
                document.getElementById("privateKey").value = "";
                document.querySelector(".download-icon").style.display = "none";
                privateKeyCountdownText.style.display = "none";
                alert("Private key đã hết hạn và bị xoá.");
            }
        }, 1000);
    }

    // download private key về máy tính.
    function downloadPriKey() {
        const privateKey = document.getElementById("privateKey").value;
        if(privateKey) {
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
            data : {
                title : title,
                publicKey : publicKey,
                userId: <%=user.getId()%>
            },
            success: function (response) {
                if (response.success) {
                    alert(response.message);
                } else {
                    alert(response.message);
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error("AJAX Error: " + textStatus + " - " + errorThrown);
                alert("Không thể thêm khoá vào cơ sở dữ liệu, vui lòng thử lại sau");
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
                    alert('Loi khi doc tep . Vui long thu lai')
                };

                reader.readAsText(file);


            }
        };
        inputFile.click();
    }

    //public key must be format pem.
    function validatePublicKey(publicKey) {
        const pemRex = /-----BEGIN PUBLIC KEY-----\n([A-Za-z0-9+/=\n]+)-----END PUBLIC KEY-----/;
        if(!pemRex.test(publicKey.trim())) {
            return false;
        }
        return true;
    }

    function upKeyIntoDB() {
        const title = document.getElementById("ukeyName").value;
        const publicKey = document.getElementById("upublicKey").value;

        if (!validatePublicKey(publicKey)) {
            alert("Public key không đúng định dạng PEM!");
            return;
        }


        $.ajax({
            method: "POST",
            url: "/HandMadeStore/add-public-key",
            data : {
                title : title,
                publicKey : publicKey,
                userId: <%=user.getId()%>
            },
            success: function (response) {
                if (response.success) {
                    alert(response.message);
                } else {
                    alert(response.message);
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error("AJAX Error: " + textStatus + " - " + errorThrown);
                alert("Không thể thêm khoá vào cơ sở dữ liệu, vui lòng thử lại sau");
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
                <button onclick="deleteKey(${key.userId})">Xoá</button>
            </div>
        `;

            keysContainer.appendChild(keyElement);
        });
    }




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

        <%--document.getElementById('productSelect').addEventListener('change', function () {--%>
        <%--    const productId = this.value;--%>
        <%--    if (productId) {--%>
        <%--        const url = '<%=request.getContextPath()%>/product-detail?id=' + productId;--%>
        <%--        window.open(url, '_blank');--%>
        <%--    }--%>
        <%--});--%>

        function goBack() {
            const previousPage = document.referrer;
            if (previousPage) {
                window.location.href = previousPage;
            } else {
                window.history.href = "/";
            }
        }


        // XỬ LÝ DÀNH CHO THÔNG TIN LỊCH SỬ ĐƠN HÀNG

        function showOrderDetails(event, clickedElement) {
            let rowIndex = 0;
            let row = clickedElement;
            while ((row = row.previousElementSibling) != null) {
                rowIndex++;
            }
            event.preventDefault();
            const orderId = clickedElement.id;
            $.ajax({
                method: "POST",
                url: "/HandMadeStore/order-ajax-handle",
                data: {
                    action: "showOrder",
                    orderId: orderId
                },
                success: function (response) {
                    let orderDetailsResponse;

                    // Kiểm tra nếu response đã là đối tượng JSON
                    if (typeof response === "string") {
                        try {
                            orderDetailsResponse = JSON.parse(response);
                        } catch (e) {
                            console.error("Response không phải là JSON hợp lệ:", response);
                            return;
                        }
                    } else {
                        orderDetailsResponse = response;
                    }
                    let showCancelBox;
                    if (orderDetailsResponse.order.status == 0 || orderDetailsResponse.order.status == 1) {
                        showCancelBox = `
                        <div>
                            <span class="text-decoration-underline text-danger" style="cursor: pointer"
                             id = "btnShowCancelOrder"
                             onclick="showCancelOrder(${orderDetailsResponse.order.id}, ${rowIndex})">Hủy đơn hàng</button>
                        </div>
                        <div id="cancelOrderBox"></div>
                    `
                    }
                    let htmlContent = `
                    <div class="row">
                        <button type="button" class="btn btn-success" onclick="hideDetailOrder()">Thoát</button>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <h3>Chi tiết sản phẩm</h3>
                            <div class="row" id="product-details" style="height: 400px;overflow-y: scroll;">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="p-3 rounded">
                                <h2>Thông tin chi tiết đơn hàng</h2>
                                <p><strong>Mã đơn hàng:</strong> ${orderDetailsResponse.order.id}</p>
                                <p><strong>Ngày đặt hàng:</strong> ${formattedDate(orderDetailsResponse.order.orderDate)}</p>
                                <p><strong>Trạng thái:</strong> <p id="status" class="text-center fw-bold fs-5">${asNameStatus(orderDetailsResponse.order.status)}</p></p>
                                <p><strong>Tên người nhận:</strong> ${orderDetailsResponse.order.consigneeName}</p>
                                <p><strong>Số điện thoại người nhận:</strong> ${orderDetailsResponse.order.consigneePhoneNumber}</p>
                                <p><strong>Địa chỉ giao:</strong> ${orderDetailsResponse.order.address}</p>
                                <p><strong>Tiền sản phẩm:</strong> ${formattedPrice(orderDetailsResponse.order.totalPrice - orderDetailsResponse.order.shippingFee)}</p>
                                <p><strong>Phí vận chuyển:</strong> ${formattedPrice(orderDetailsResponse.order.shippingFee)}</p>
                                <p><strong>Tổng tiền:</strong> ${formattedPrice(orderDetailsResponse.order.totalPrice)}</p>
                                <p><strong>Ghi chú:</strong> ${orderDetailsResponse.order.note?orderDetailsResponse.order.note:""}</p>
                                ` + (showCancelBox ? showCancelBox : "") + `
                            </div>
                        </div>
                    </div>
                `;

                    // Tạo HTML cho danh sách sản phẩm
                    let productDetailsHtml = '';
                    orderDetailsResponse.orderDetailMappings.forEach(function (orderDetailMapping) {
                        productDetailsHtml += `
                        <div class="col-md-12 mb-4">
                            <div class="card">
                                <div class="row">
                                    <img src="<%=request.getContextPath()+"/"%>${orderDetailMapping.imagePath}" class="col-4" width="100px" alt="Product Image">
                                    <div class="col">
                                        <h5 class="card-title">${orderDetailMapping.productName}</h5>
                                        <p class="card-text" style="margin-bottom:0;">Đơn giá: ${formattedPrice(orderDetailMapping.orderDetail.finalSellingPrice)}</p>
                                        <p class="card-text" style="margin-bottom:0;">X ${orderDetailMapping.orderDetail.quantity}</p>
                                        <p class="card-text fw-bold text-danger" style="margin-bottom:0;">${formattedPrice(orderDetailMapping.orderDetail.finalSellingPrice * orderDetailMapping.orderDetail.quantity)}</p>
                                        <a href="<%=request.getContextPath()%>/product-detail?id=${orderDetailMapping.orderDetail.productId}" style="text-decoration: none;">Xem, Đánh giá trực tiếp sản phẩm</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                    });

                    // Hiển thị thông tin chi tiết đơn hàng
                    $('#detailOrder').addClass('row w-75 h-75 p-3 rounded');
                    $('#detailOrder').css('display', 'block');
                    $('#detailOrder').html(htmlContent);
                    // Hiển thị danh sách sản phẩm
                    $('#product-details').html(productDetailsHtml);

                },
                error: function () {
                    alert("error")
                }
            })
        }

        function more(userId, limit) {
            const statusNumber = document.getElementById("statusNumber").value;
            const currentOffset = document.getElementById("showCouter").value;
            $.ajax({
                method: "POST",
                url: "/HandMadeStore/order-ajax-handle",
                data: {
                    action: "moreOrder",
                    userId: userId,
                    limit: limit,
                    offset: currentOffset,
                    statusNumber: statusNumber
                },
                success: function (response) {
                    const allOrderNumber = document.getElementById("allOrderNumber");
                    const showCouter = document.getElementById("showCouter");
                    showCouter.value = parseInt(showCouter.value, 10) + response.length;
                    renderOrders(response);
                    if (parseInt(showCouter.value, 10) < allOrderNumber.value) {
                        const more = $('#more');
                        if (!more.length) {
                            $('#morebox').html(`
                        <span id="more" class="fst-italic fw-bold" style="cursor: pointer"
                          onclick="more(${userId}, ${limit})"
                        >Xem thêm...
                        </span>
                    `);
                        }
                    } else {
                        const more = $('#more');
                        if (more.length) {
                            more.remove();
                        }
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("Error details:", jqXHR, textStatus, errorThrown);
                    alert("Load more failed!");
                }
            })
        }

        function orderFilter(userId, limit, statusNumber) {
            $('#data tbody').empty();
            document.getElementById("statusNumber").value = statusNumber;
            const allOrderNumber = document.getElementById("allOrderNumber");
            const showCouter = document.getElementById("showCouter");
            // thay đổi allOrderNumber và showCouter
            $.ajax({
                method: "POST",
                url: "/HandMadeStore/order-ajax-handle",
                data: {
                    action: "getNumberFilter",
                    userId: userId,
                    statusNumber: statusNumber
                },
                success: function (response) {
                    allOrderNumber.value = response;
                    showCouter.value = 0;
                    if (parseInt(showCouter.value, 10) < allOrderNumber.value) {
                        const more = $('#more');
                        if (!more.length) {
                            $('#morebox').html(`
                        <span id="more" class="fst-italic fw-bold" style="cursor: pointer"
                          onclick="more(${userId}, ${limit})"
                        >Xem thêm...
                        </span>
                    `);
                        }
                    } else {
                        const more = $('#more');
                        if (more.length) {
                            more.remove();
                        }
                    }
                    more(userId, limit, statusNumber)
                },
                error: function () {
                    console.log("Load more failed!");
                    more(userId, limit, statusNumber)
                }
            })
        }

        function renderOrders(ordersResponse) {
            ordersResponse.forEach(function (orderResponse) {
                const order = orderResponse.order;
                const imagePaths = orderResponse.image_name;

                let firstCol = '';
                for (let key in imagePaths) {
                    if (imagePaths.hasOwnProperty(key)) {
                        firstCol += `
                    <div class="d-flex mb-2">
                            <div>
                            <img src="<%=request.getContextPath()+"/"%>${imagePaths[key]}"
                                     alt="" width="100px">
                            </div>
                            <div style="width: 150px; display:flex; align-items: center">
                                 ${key}
                            </div>
                    </div>`;
                    }
                }
                let backgroundColor = '';
                let statusText = '';
                switch (order.status) {
                    case 0://isWaitConfirmOrder
                        backgroundColor = '#ffcc00';
                        statusText = 'Chờ xác nhận';
                        break;
                    case 1://isPreparing
                        backgroundColor = '#5F9EA0';
                        statusText = 'Đang đóng gói';
                        break;
                    case 2://isDeliveringOrder
                        backgroundColor = '#0171d3';
                        statusText = 'Đang giao';
                        break;
                    case 3://isSucccessfulOrder
                        backgroundColor = '#4d8a54';
                        statusText = 'Thành công';
                        break;
                    case 4://isCanceledOrder
                        backgroundColor = '#ff0000';
                        statusText = 'Đã hủy';
                        break;
                    default:
                        backgroundColor = '#ffffff';
                        statusText = 'Không xác định';
                        break;
                }
                const orderHtml = `
        <tr class="text-center" style="cursor: pointer;" onclick="showOrderDetails(event, this)" id="${order.id}">
            <td class="geeks" style="border-top: 1px solid #c9c9c9;border-bottom: 1px solid #c9c9c9; vertical-align: middle">${firstCol}</td>
            <td class="geeks " style="border-top: 1px solid #c9c9c9;border-bottom: 1px solid #c9c9c9; vertical-align: middle" >${order.address}</td>
            <td class="geeks" style="border-top: 1px solid #c9c9c9;border-bottom: 1px solid #c9c9c9; vertical-align: middle">${formattedDate(order.orderDate)}</td>
            <td class="geeks" style="border-top: 1px solid #c9c9c9;border-bottom: 1px solid #c9c9c9; vertical-align: middle">${formattedPrice(order.totalPrice)}</td>
            <td class="geeks" style="background-color: ${backgroundColor}; color: #ffffff;vertical-align: middle">${statusText}</td>
        </tr>
        `;

                $('#data tbody').append(orderHtml);
            });
        }

        function hideDetailOrder() {
            $('#detailOrder').css('display', 'none');
        }

        function formattedDate(value) {
            // Định dạng ngày giờ theo yyyy-MM-dd HH:mm:ss.S
            const orderDate = new Date(value);
            const year = orderDate.getFullYear();
            const month = String(orderDate.getMonth() + 1).padStart(2, '0');
            const day = String(orderDate.getDate()).padStart(2, '0');
            const hours = String(orderDate.getHours()).padStart(2, '0');
            const minutes = String(orderDate.getMinutes()).padStart(2, '0');
            const seconds = String(orderDate.getSeconds()).padStart(2, '0');
            const milliseconds = String(orderDate.getMilliseconds()).padStart(3, '0');
            const formattedDate = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}.${milliseconds}`;
            return formattedDate;
        }

        function formattedPrice(value) {
            const formattedPrice = new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(value);
            return formattedPrice;
        }

        function asNameStatus(statusNumber) {
            const numericStatus = parseInt(statusNumber, 10); // Hoặc Number(status)
            let statusText = '';
            switch (numericStatus) {
                case 0://isWaitConfirmOrder
                    statusText = 'Chờ xác nhận';
                    break;
                case 1://isPreparing
                    statusText = 'Đang đóng gói';
                    break;
                case 2://isDeliveringOrder
                    statusText = 'Đang giao';
                    break;
                case 3://isSucccessfulOrder
                    statusText = 'Thành công';
                    break;
                case 4://isCanceledOrder
                    statusText = 'Đã hủy';
                    break;
                default:
                    statusText = 'Không xác định';
                    break;
            }
            return statusText;
        }

        function asColorStatus(statusNumber) {
            let backgroundColor = '';
            switch (statusNumber) {
                case 0://isWaitConfirmOrder
                    backgroundColor = '#ffcc00';
                    break;
                case 1://isPreparing
                    backgroundColor = '#5F9EA0';
                    break;
                case 2://isDeliveringOrder
                    backgroundColor = '#0171d3';
                    break;
                case 3://isSucccessfulOrder
                    backgroundColor = '#4d8a54';
                    break;
                case 4://isCanceledOrder
                    backgroundColor = '#ff0000';
                    break;
                default:
                    backgroundColor = '#ffffff';
                    break;
            }
            return backgroundColor;
        }

        function showCancelOrder(orderId, rowIndex) {
            let cancelOrderBox = `
                        <p class="fst-italic text-body text-decoration-none fw-normal">Quý khách KHOAN hãy hủy đơn hàng.Nếu quý khách hàng có bất cứ vấn đề với đơn hàng của mình xin vui lòng liên hệ tổng đài HandMadeStore qua đường dây nóng 0336677141.Cám ơn quý khách!</p>
                        <div class="mb-3">
                          <label for="cancelReason" class="form-label">Lý do hủy đơn hàng</label>
                          <textarea class="form-control" id="cancelReason" rows="2"></textarea>
                        </div>
                        <button type="button" class="btn btn-danger" onclick="cancelOrder(${orderId}, ${rowIndex})">Hủy đơn hàng</button>
                        <div id="loadingCancelBox" style="display:none;">
                            <span class="spinner-border spinner-border-sm" role="status"
                                  aria-hidden="true"></span>
                            Đang hủy bỏ đơn hàng
                        </div>
                    `;
            if ($('#cancelOrderBox')) {
                $('#cancelOrderBox').html(cancelOrderBox)

                if ($('#cancelOrderBox').css('display') === 'none') {
                    $('#cancelOrderBox').css('display', 'block');
                } else {
                    $('#cancelOrderBox').css('display', 'none');
                }
            }
        }

        function cancelOrder(orderId, rowIndex) {
            let cancelReason;
            if ($('#cancelReason')) {
            }
            if ($('#loadingCancelBox')) {
                $('#loadingCancelBox').css('display', 'block');
            }
            $.ajax({
                method: "POST",
                url: "/HandMadeStore/order-ajax-handle",
                data: {
                    action: "customerCancelOrder",
                    orderId: orderId,
                    cancelReason: cancelReason
                },
                success: function (response) {
                    if ($('#status')) {
                        console.log(response)
                        $('#status').html(asNameStatus(response));
                    }
                    if ($('#cancelOrderBox')) {
                        $('#cancelOrderBox').remove()
                    }
                    if ($('#btnShowCancelOrder')) {
                        $('#btnShowCancelOrder').remove()
                    }
                    console.log("rowIndex " + rowIndex)
                    $('#data tbody tr').eq(rowIndex).remove();
                },
                error: function () {
                    alert("Hủy đơn hàng thất bại!");
                }
            })
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