<%@ page import="model.service.ImageService" %>
<%@ page import="model.bean.*" %>
<%@ page import="model.dao.KeyDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <link href="<%=request.getContextPath()%>/views/ReportPKey/reportKey.css" rel="stylesheet" type="text/css">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"> <!--icon-->
    <title>Cửa Hàng HandMade</title>
</head>
<body>


<div class="header">
    <%@include file="/views/MenuBar/menu.jsp" %>
</div>

<%
    User user = (User) (request.getSession().getAttribute("auth"));
    if (user != null) {
%>
<%
    List<Key> publicKeys = KeyDAO.getPublicKey(user.getId());
%>
<div class="content_report my-3 " id="containerFrom">
    <div id="formReport" class=" d-flex justify-content-center align-items-center">
        <form class="p-3 " id="formReportKey">
            <div class="text-center">
                <p class="fw-bold fs-4">Báo Mất Khóa</p>
            </div>
            <div class="publicKeyList d-flex my-4 mt-2">
                <p class="elementForm p-2 mb-0">Public Key</p>
                <select name="dropdown" id="dropdown" class="elementInput" required>
                    <option value="" disabled selected>-- Chọn khóa bị lộ Key --</option>
                    <%for(Key key : publicKeys){%>
                        <option value="<%=key.getTitle()%>"><%=key.getTitle()%></option>
                    <%}%>
                </select>
            </div>
            <div class="disclosureDate d-flex my-4 ">
                <p class=" elementForm mb-0 p-2">Ngày Lộ Key</p>
                <input class="elementInput" type="datetime-local" id="inputDisclosureDate" name="inputDate" required>
            </div>
            <div class=" my-4">
                <p class="mb-0 fw-bold mb-2"><u>Hoàn Cảnh Lộ Key(không bắt buộc) :</u></p>
                <textarea name="reasonable" id="inputSituation" style="width: 98% ; height: 10em" placeholder="Nhập lý do ở đây..."></textarea>
            </div>
            <div>
                <label class="fw-bold">Nhập Mã Xác Thực :</label>
                <input type="text" id="authenticate" name="authenticate" placeholder="Nhập mã xác thực..." style="width: 57%;" >
                <button id="buttonOTP">Lấy Mã Qua Email</button>
            </div>

            <div class=" my-4 d-flex">
                <input style="width: 20px" type="checkbox" id="checkbox" name="checkbox" required>
                <p class="mb-0 ms-2">Tôi cam kết những thông tin cung cấp là hoàn toàn chính xác và trung thực, chịu trách nhiệm pháp
                    lý.</p>
            </div>
            <div class="text-center">
                <button id="submitContinue" type="submit" class="fw-bold p-2"  >Tiếp Tục >></button>
            </div>
        </form>
    </div>
</div>
<%--Xác minh đơn hàng--%>
<div id="verifyOrder" class="d-flex justify-content-center align-items-center my-3 " >
</div>

<%}else{%>
<div class="text-danger text-center fw-bold fs-4 mb-5" >Chưa đăng nhập không có dữ liệu</div>
<%}%>

<div class="footer" >
    <%@include file="../Footer/footer.jsp" %>
</div>
</body>
   <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<script>

    $('#buttonOTP').on('click', function(event) {
        event.preventDefault();
        console.log("Click OKE")
        $.ajax({
            url: "/HandMadeStore/reportKey",
            type: 'POST',
            data: {
                continueReport: "otp",
            },
            success: function(response) {
                if(response === "sendSuccess"){
                    Swal.fire({
                        position: "center",
                        icon: "success",
                        title: "Gửi mã qua mail thành công!",
                        showConfirmButton: false,
                        timer: 1500
                    });
                }
            },
            error: function(xhr, status, error) {
            }
        });
    });


    const cancelledOrders = []; // Hàm lưu các id order Hủy đơn
    $(document).ready(function() {

        $('#submitContinue').click(function(event) {
            console.log("zô click")
            event.preventDefault();
            let publicKey =  $('#dropdown').val();
            let inputDisclosureDate = $('#inputDisclosureDate').val();
            if(!publicKey || !inputDisclosureDate){
                Swal.fire({
                    position: "center",
                    icon: "error",
                    title: "Cần điền đầy đủ thông tin!",
                    showConfirmButton: false,
                    timer: 1500
                });
                return;
            }
                // Gửi dữ liệu qua AJAX
                $.ajax({
                    url: "/HandMadeStore/reportKey", // URL của controller xử lý
                    type: 'POST',
                    data: {
                        publicKey: $('#dropdown').val(),
                        inputDisclosureDate: $('#inputDisclosureDate').val(),
                        inputSituation: $('#inputSituation').val(),
                        continueReport: "true",
                        inputOTP: $('#authenticate').val()
                    },
                    success: function(response) {
                        if(response === "incorrect") {
                            Swal.fire({
                                position: "center",
                                icon: "error",
                                title: "Mã OTP bị sai!",
                                showConfirmButton: false,
                                timer: 1500
                            });
                        }else{
                            $('#containerFrom').hide();
                            $('#verifyOrder').show().html(response);
                        }

                    },
                    error: function(xhr, status, error) {
                        // Xử lý khi có lỗi
                        alert('Error: ' + xhr.responseText);
                    }
                });

        });
    });


    let count = 0;


    function showTick(idOrder,button) {
        // Ẩn tất cả các nút
        const buttonClick = button.id;

        count++;
        const verifyTrue = document.getElementById("verifyTrue"+idOrder);
        const verifyfalse = document.getElementById("verifyFalse"+idOrder);

        verifyTrue.style.display = 'none';
        verifyfalse.style.display = 'none';
        // Hiển thị icon tick
        const tickIcon = document.getElementById("tickIcon"+idOrder);
        tickIcon.style.display = 'block';

        if(buttonClick === "verifyFalse"+idOrder){
            cancelledOrders.push(idOrder);
        }
        const countOrder = document.getElementById("countOrder");
        let countOr = countOrder.textContent;
        const reportKey = document.getElementById("reportKey");
        if(count == countOr){
            reportKey.disabled = false;
            reportKey.style.background = 'red';
            reportKey.style.color = 'white';
            console.log(cancelledOrders)
        }
    }


    function ReportKey(){
        const cancelledOrdersString = cancelledOrders.join(",");

        // Gửi dữ liệu qua AJAX
        $.ajax({
            url: "/HandMadeStore/reportKey", // URL của controller xử lý
            type: 'POST',
            data: {
                publicKey: $('#dropdown').val(),
                inputDisclosureDate: $('#inputDisclosureDate').val(),
                inputSituation: $('#inputSituation').val(),
                continueReport: "report",
                idOrderCancel: cancelledOrdersString
            },
            success: function(response) {
                console.log("zô")
                Swal.fire({
                    position: "center",
                    icon: "success",
                    title: "Hoàn Thành Report Key",
                    showConfirmButton: false,
                    timer: 1500
                }).then (() => {
                    window.location.href = response.redirect;
                });
            },
            error: function(xhr, status, error) {
                // Xử lý khi có lỗi
                alert('Error: ' + xhr.responseText);
            }
        });

    }


</script>


</html>
