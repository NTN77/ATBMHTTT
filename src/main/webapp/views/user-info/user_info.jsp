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
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
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
        .content {
            height: 100vh;
            overflow: hidden;
        }
        iframe {
            border: none;
            width: 100%;
            height: 100%;
        }
    </style>
</head>
<% String framePath = ((String) request.getAttribute("framePath") == null) ?
        request.getContextPath() + "/views/user-info/change_user_info.jsp"
        : (request.getContextPath() + (String) request.getAttribute("framePath"));
%>
<% String mypage = (String) request.getAttribute("page");
    mypage = (mypage == null) ? "information" : mypage;%>
<%
    User user = (User) (request.getSession().getAttribute("auth"));
    if (user != null) {
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
                    <%!
                        String styleClick1 = "";
                        String styleClick2 = "";
                        String styleClick3 = "";
                    %>
                    <%
                        if ("information".equals(mypage)) {
                            styleClick1 = "active";
                            styleClick2 = "";
                            styleClick3 = "";
                        } else if ("security-order".equals(mypage)) {
                            styleClick2 = "active";
                            styleClick1 = "";
                            styleClick3 = "";
                        } else if ("order-history".equals(mypage)) {
                            styleClick3 = "active";
                            styleClick1 = "";
                            styleClick2 = "";
                        }
                    %>
                    <div class="nav flex-column nav-pills" id="profileTab" role="tablist" aria-orientation="vertical">
                        <a class="nav-link <%=styleClick1%>" id="info-tab" data-bs-toggle="pill" href="<%=request.getContextPath()%>/user-info?page=information" role="tab"
                           aria-controls="info" aria-selected="true">Thông tin cá nhân</a>
                        <a class="nav-link <%=styleClick2%>" id="security-tab" data-bs-toggle="pill" href="<%=request.getContextPath()%>/user-info?page=security-order" role="tab"
                           aria-controls="security" aria-selected="false">Thiết lập bảo mật</a>
                        <a class="nav-link <%=styleClick3%>" id="order-history-tab" data-bs-toggle="pill" href="<%=request.getContextPath()%>/user-info?page=order-history"
                           role="tab" aria-controls="order-history" aria-selected="false">Lịch sử mua hàng</a>
                    </div>
                </div>


                <!-- Nội dung của các tab -->
                <div class="col-12 col-md-9 content">
                    <iframe src="<%=framePath%>"
                            frameborder="0" name="content" id="frame" width="100%"
                            scrolling="yes"
                            class="container-fluid "
                    ></iframe>
                </div>
            </div>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="https://cdn.jsdelivr.net/npm/moment@2.29.1/moment.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/moment@2.29.1/locale/vi.js"></script>
<script>
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