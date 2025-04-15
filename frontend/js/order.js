document.addEventListener("DOMContentLoaded", function () {
    initializeOrderManagement();
});

const request = axios.create({
    baseURL: "http://localhost:8080/api/",
    headers: {
        token: localStorage.getItem('token')
    }
});

let currentPage = 1;
const pageSize = 10;

function initializeOrderManagement() {
    const orderElement = document.getElementById("orders");
    if (!orderElement) {
        console.error("Orders element not found");
        return;
    }
    const paginationElement = document.createElement("div");
    paginationElement.className = "d-flex justify-content-center mt-4 pagination-custom";
    orderElement.after(paginationElement);

    // Load initial orders
    getOrders(1);

    // Setup event listeners for search and date inputs, if they exist
    const searchElement = document.getElementById("search");
    if (searchElement) {
        searchElement.addEventListener("keyup", function (event) {
            if (event.key === "Enter") {
                getFilteredOrders();
            }
        });
    }
    const dateElement = document.getElementById("date");
    if (dateElement) {
        dateElement.addEventListener("change", function () {
            getFilteredOrders();
        });
    }
}

function getOrders(page) {
    currentPage = page;
    request.get("orders", {
        params: {
            page: page,
            size: pageSize
        }
    })
        .then(response => {
            drawOrders(response.data.orders);
            drawPagination(response.data.totalPages);
        })
        .catch(error => {
            console.error("Error fetching orders:", error);
            showError("Failed to load orders");
        });
}

function getFilteredOrders() {
    const search = document.getElementById("search")?.value || "";
    const date = document.getElementById("date")?.value || "";
    request.get("orders", {
        params: {
            search: search,
            date: date,
            page: 1, // Reset to first page when filtering
            size: pageSize
        }
    })
        .then(response => {
            drawOrders(response.data.orders);
            drawPagination(response.data.totalPages);
            currentPage = 1;
        })
        .catch(error => {
            console.error("Error fetching filtered orders:", error);
            showError("Failed to filter orders");
        });
}

function drawOrders(orders) {
    const orderElement = document.getElementById("orders");
    let html = "";
    if (!orders || orders.length === 0) {
        html = `<div class="alert alert-info">No orders found</div>`;
    } else {
        orders.forEach(order => {
            html += `
            <div class="card order-card mb-3 p-3">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6>Order #${order.id}</h6>
                        <p class="text-muted">${order.locationId ? `Location ID: ${order.locationId}` : 'No location'}</p>
                    </div>
                    <span class="badge bg-secondary ms-2">${order.date || 'No date'}</span>
                    <select class="form-select status-select" data-order-id="${order.id}" style="width: 200px">
                        <option value="NEW" ${order.status === "NEW" ? "selected" : ""}>NEW</option>
                        <option value="PROCESSING" ${order.status === "PROCESSING" ? "selected" : ""}>PROCESSING</option>
                        <option value="DELIVERING" ${order.status === "DELIVERING" ? "selected" : ""}>DELIVERING</option>
                        <option value="DELIVERED" ${order.status === "DELIVERED" ? "selected" : ""}>DELIVERED</option>
                        <option value="CANCELLED" ${order.status === "CANCELLED" ? "selected" : ""}>CANCELLED</option>
                    </select>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-2">
                    <p class="mb-0">Total: <strong>$${order.total || 0}</strong></p>
                    <button class="btn btn-primary btn-custom toggle-view" data-order-id="${order.id}">
                        <i class="fas fa-eye"></i> View Items
                    </button>
                </div>
                <div class="order-details mt-3" id="order-details-${order.id}" style="display: none;">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <p><strong>Items:</strong></p>
                        <button class="btn btn-sm btn-outline-secondary" onclick="loadOrderItems(${order.id})">
                            <i class="fas fa-sync-alt"></i> Refresh
                        </button>
                    </div>
                    <ul class="list-group" id="order-items-${order.id}">
                        <li class="list-group-item text-center">Loading items...</li>
                    </ul>
                </div>
            </div>`;
        });
    }
    orderElement.innerHTML = html;
    setupEventListeners();
}

function setupEventListeners() {
    // Status change listener
    document.querySelectorAll('.status-select').forEach(select => {
        select.addEventListener('change', function () {
            const orderId = this.dataset.orderId;
            const newStatus = this.value;
            updateOrderStatus(orderId, newStatus);
        });
    });

    // Toggle view listener for order details
    document.querySelectorAll('.toggle-view').forEach(button => {
        button.addEventListener('click', function () {
            const orderId = this.dataset.orderId;
            const detailsDiv = document.getElementById(`order-details-${orderId}`);
            if (detailsDiv.style.display === 'none' || detailsDiv.style.display === '') {
                detailsDiv.style.display = 'block';
                loadOrderItems(orderId);
            } else {
                detailsDiv.style.display = 'none';
            }
        });
    });
}

function loadOrderItems(orderId) {
    const itemsContainer = document.getElementById(`order-items-${orderId}`);
    request.get(`orders/${orderId}/items`)
        .then(response => {
            const items = response.data;
            let html = "";
            if (!items || items.length === 0) {
                html = '<li class="list-group-item text-center">No items found</li>';
            } else {
                items.forEach(item => {
                    const totalPrice = item.price * item.quantity;
                    html += `
                <li class="list-group-item d-flex justify-content-between align-items-center">
                    <div>
                        <strong>${item.productName}</strong> (ID: ${item.productId})
                        <br>
                        <small>Price: $${item.price} x ${item.quantity} = $${totalPrice.toFixed(2)}</small>
                    </div>
                </li>`;
                });
            }
            itemsContainer.innerHTML = html;
        })
        .catch(error => {
            console.error(`Error loading items for order ${orderId}:`, error);
            itemsContainer.innerHTML = '<li class="list-group-item text-danger">Failed to load items</li>';
        });
}

function updateOrderStatus(orderId, status) {
    request.post('orders/status', {
        orderId: parseInt(orderId),
        status: status
    })
        .then(() => {
            showSuccess(`Order #${orderId} status updated to ${status}`);
        })
        .catch(error => {
            console.error(`Error updating status for order ${orderId}:`, error);
            showError("Failed to update order status");
            // Agar kerak bo'lsa, avvalgi statusni tiklash uchun qo'shimcha kod yozilishi mumkin
        });
}

function drawPagination(totalPages) {
    const paginationElement = document.querySelector('.pagination-custom');
    let html = '<nav><ul class="pagination">';

    // Previous button
    html += `<li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="getOrders(${currentPage - 1}); return false;">Previous</a>
            </li>`;

    // Page numbers
    for (let i = 1; i <= totalPages; i++) {
        html += `<li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="getOrders(${i}); return false;">${i}</a>
                </li>`;
    }

    // Next button
    html += `<li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="getOrders(${currentPage + 1}); return false;">Next</a>
            </li>`;

    html += '</ul></nav>';
    paginationElement.innerHTML = html;
}

function showSuccess(message) {
    const alert = document.createElement('div');
    alert.className = 'alert alert-success alert-dismissible fade show position-fixed top-0 end-0 m-3';
    alert.style.zIndex = '9999';
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    document.body.appendChild(alert);
    setTimeout(() => {
        alert.classList.remove('show');
        setTimeout(() => alert.remove(), 150);
    }, 3000);
}

function showError(message) {
    const alert = document.createElement('div');
    alert.className = 'alert alert-danger alert-dismissible fade show position-fixed top-0 end-0 m-3';
    alert.style.zIndex = '9999';
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    document.body.appendChild(alert);
    setTimeout(() => {
        alert.classList.remove('show');
        setTimeout(() => alert.remove(), 150);
    }, 3000);
}
