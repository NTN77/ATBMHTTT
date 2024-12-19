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
<!-- Tab Lịch sử mua hàng -->
<div class="tab-pane fade show active" id="order-history" role="tabpanel"
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

<div id="detailOrder" style="display:none;">
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