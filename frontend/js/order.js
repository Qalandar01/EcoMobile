let orderElement = document.getElementById("orders");
let paginationElement = document.createElement("div");
paginationElement.className = "d-flex justify-content-center mt-4";
orderElement.after(paginationElement);

document.addEventListener("DOMContentLoaded", function () {
    getOrders(1);
});

let orders = [];
let currentPage = 1;
const pageSize = 10; // Number of orders per page

function getOrders(page) {
    currentPage = page;

    request({
        url: "order",
        method: "GET",
        params: {
            token: localStorage.getItem("token"),
            page: page,
            size: pageSize
        }
    }).then(res => {
        orders = res.data.orders; // Ensure your backend response includes `{ orders: [], totalPages: X }`
        draw(orders);
        drawPagination(res.data.totalPages);
    }).catch(ex => {
        console.error("Error fetching orders:", ex);
    });
}

function getFilteredOrders(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const search = formData.get("search");
    const date = formData.get("date");

    request({
        url: "order",
        method: "get",
        params: {
            search: search || "",
            date: date || "",
            page: 1, // Reset to first page when filtering
            size: pageSize
        }
    }).then(res => {
        orders = res.data.orders;
        draw(orders);
        drawPagination(res.data.totalPages);
    }).catch(err => {
        console.error("Error fetching orders:", err);
    });
}

function draw(orders) {
    let s = "";
    for (let order of orders) {
        s += `
       <div class="card order-card mb-3 p-3">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h6>Order #${order.id}</h6>
                    <p class="text-muted">${order.location}</p>
                </div>
                <span class="badge bg-secondary ms-2">${order.date}</span> 
                <select style="width: 500px" class="form-select status-select" data-order-id="${order.id}">
                    <option value="NEW" ${order.status === "NEW" ? "selected" : ""}>NEW</option>
                    <option value="PROCESSING" ${order.status === "PROCESSING" ? "selected" : ""}>PROCESSING</option>
                    <option value="DELIVERING" ${order.status === "DELIVERING" ? "selected" : ""}>DELIVERING</option>
                    <option value="DELIVERED" ${order.status === "DELIVERED" ? "selected" : ""}>DELIVERED</option>
                    <option value="CANCELLED" ${order.status === "CANCELLED" ? "selected" : ""}>CANCELLED</option>
                </select>
            </div>
            <div class="d-flex justify-content-between align-items-center mt-2">
                <p class="mb-0">Total: <strong>$ ${order.total}</strong></p>
                <button class="btn btn-primary btn-custom toggle-view" onclick="drawOrderItems(${order.id})" data-target="#order${order.id}">
                    <i class="fas fa-eye"></i> View
                </button>
            </div>
            <div class="order-details" id="order${order.id}">
                <p><strong>Items:</strong></p>
                <ul class="list-group" id="items-${order.id}">
                </ul>
            </div>
        </div>`;
    }
    orderElement.innerHTML = s;
    attachEventListeners();
}

function drawOrderItems(orderId) {
    let order = orders.find(o => o.id === orderId);
    let s = "";
    if (!order) return;
    let itemsContainer = document.getElementById("items-" + orderId);
    for (let orderItem of order.orderItems) {
        let totalPrice = orderItem.product.price * orderItem.quantity;
        s += `
          <li class="list-group-item">
            ${orderItem.product.name} - Size ${orderItem.product.size.productSize} - Qty: ${orderItem.quantity} 
            - <strong>$${totalPrice}</strong>
          </li>
        `;
    }

    if (!order.orderItems){
        s = 'No Items';
    }
    itemsContainer.innerHTML = s;
}

function attachEventListeners() {
    document.querySelectorAll(".status-select").forEach(select => {
        select.addEventListener("change", function () {
            const orderId = this.dataset.orderId;
            const newStatus = this.value;
            updateStatusInBackend(orderId, newStatus);
            updateStatus(orderId, newStatus, this);
        });
    });

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
}

function updateStatus(orderId, status, selectElement) {
    const statusClasses = {
        "PROCESSING": "bg-warning",
        "DELIVERED": "bg-success",
        "CANCELLED": "bg-danger",
        "NEW": "bg-info",
        "DELIVERING": "bg-primary"
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

function updateStatusInBackend(orderId, status) {
    request({
        url: "order/status",
        method: "POST",
        data: {
            orderId,
            status
        }
    }).catch(err => console.error("Error updating status:", err));
}

function drawPagination(totalPages) {
    let s = `<nav><ul class="pagination">`;

    s += `<li class="page-item ${currentPage === 1 ? "disabled" : ""}">
            <a class="page-link" href="#" onclick="getOrders(${currentPage - 1})">Previous</a>
          </li>`;

    for (let i = 1; i <= totalPages; i++) {
        s += `<li class="page-item ${i === currentPage ? "active" : ""}">
                <a class="page-link" href="#" onclick="getOrders(${i})">${i}</a>
              </li>`;
    }

    s += `<li class="page-item ${currentPage === totalPages ? "disabled" : ""}">
            <a class="page-link" href="#" onclick="getOrders(${currentPage + 1})">Next</a>
          </li>`;

    s += `</ul></nav>`;
    paginationElement.innerHTML = s;
}

const searchInput = document.querySelector(".search-bar");

searchInput.addEventListener("focus", () => {
    searchInput.style.width = "500px";
    searchInput.style.boxShadow = "0px 4px 8px rgba(0, 0, 0, 0.1)";
});

searchInput.addEventListener("blur", () => {
    if (!searchInput.value) {
        searchInput.style.width = "200px";
        searchInput.style.boxShadow = "none";
    }
});
