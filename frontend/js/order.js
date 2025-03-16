
let orderElement = document.getElementById("orders");
function getOrders(){
    let orders = [];

    request({
        url:"/order",
        method:"GET"
    }).then(res=>{
        orders = res.data;
        draw(orders);
    })
}
function draw(orders){
    let s = '';
    for (let order of orders) {
        s += `
       <div class="card order-card mb-3 p-3">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6>Order #${order.id}</h6>
                        <p class="text-muted">${order.location}</p>
                    </div>
                    <select class="form-select status-select" data-order-id="${order.id}">
                        <option value="NEW" selected>NEW</option>
                        <option value="PROCESSING" >PROCESSING</option>
                        <option value="DELIVERING">DELIVERING</option>
                        <option value="DELIVERED">DELIVERED</option>
                        <option value="CANCELLED">CANCELLED</option>
                    </select>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-2">
                    <p class="mb-0">Total: <strong>$${order.total}</strong></p>
                    <button onclick="drawOrderItems(order.id,order.orderItems)" class="btn btn-primary btn-custom toggle-view" data-target="#order${order.id}">
                        <i class="fas fa-eye"></i> View
                    </button>
                </div>
                <div class="order-details" id="order${order.id}">
                    <p><strong>Items:</strong></p>
                    <ul class="list-group" id="items-${order.id}">
                       
                    </ul>
                </div>
            </div>
        `;
    }
    orderElement.innerHTML = s;
}
function drawOrderItems(orderId,orderItems){
    let items = document.getElementById("items-"+orderId);
    let s = '';
    for (let orderItem of orderItems) {
        s += `
          <li class="list-group-item">${orderItems.product.name} - Size ${orderItem.product.size} - Qty: ${orderItem.quantity} - <strong>$${orderItem.map(item=>item.product.price * item.quantity)}</strong></li>
        `
    }

}
document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".status-select").forEach(select => {
        select.addEventListener("change", function () {
            const orderId = this.dataset.orderId;
            const newStatus = this.value;
            updateStatusInBackend(orderId,newStatus);
            updateStatus(orderId, newStatus, this);
        });
    });

    function updateStatus(orderId, status, selectElement) {
        const statusClasses = {
            "PROCESSING": "bg-warning",
            "DELIVERED": "bg-success",
            "CANCELLED": "bg-danger",
            "NEW":"bg-info",
            "DELIVERING":"bg-primary"
        };

        let badge = selectElement.closest(".order-card").querySelector(".status-badge");
        if (!badge) {
            badge = document.createElement("span");
            badge.className = "status-badge";
            selectElement.closest(".order-card").querySelector(".d-flex").appendChild(badge);
        }

        badge.textContent = status;
        badge.className = `status-badge ${statusClasses[status]}`;
    }

    // Smooth Toggle View
    document.querySelectorAll(".toggle-view").forEach(button => {
        button.addEventListener("click", function () {
            const target = document.querySelector(this.dataset.target);
            if (target.classList.contains("show")) {
                target.classList.remove("show");
            } else {
                document.querySelectorAll(".order-details").forEach(el => el.classList.remove("show"));
                target.classList.add("show");
            }
        });
    });
});

function updateStatusInBackend(orderId,status){
    request({
        url:"order/status"
    })
}