// Initialize notification client
let notificationClient = null

// Declare variables
const userId = null
const token = null
let notifications = []
let unreadNotificationsCount = 0

// Load notifications
function loadNotifications() {
    axios
        .get(`http://localhost:8080/api/notifications/user/${userId}`, {
            headers: { token: token },
        })
        .then((response) => {
            notifications = response.data

            // Count unread notifications
            unreadNotificationsCount = notifications.filter((n) => !n.read).length

            // Update notification badge
            document.getElementById("notificationCount").textContent = unreadNotificationsCount

            // Update notification list
            updateNotificationList()

            // Initialize real-time notifications after loading existing ones
            initializeRealTimeNotifications()
        })
        .catch((error) => {
            console.error("Error loading notifications:", error)
        })
}

// Initialize real-time notification client
function initializeRealTimeNotifications() {
    if (!notificationClient && userId && token) {
        notificationClient = new NotificationClient(userId, token)

        // Set callback for new notifications
        notificationClient.onNotification((notification) => {
            // Add to notifications array
            notifications.unshift(notification)
            unreadNotificationsCount++

            // Update notification badge and list
            document.getElementById("notificationCount").textContent = unreadNotificationsCount
            updateNotificationList()

            // Show toast notification
            showToast(
                notification.message,
                getNotificationToastType(notification.type),
                getNotificationTitle(notification.type),
            )
        })

        // Connect to notification stream
        notificationClient.connect()
    }
}

// Get toast type based on notification type
function getNotificationToastType(type) {
    switch (type) {
        case "ORDER_STATUS":
            return "primary"
        case "PRODUCT":
            return "success"
        case "PROMO":
            return "warning"
        default:
            return "info"
    }
}

// Get notification title based on type
function getNotificationTitle(type) {
    switch (type) {
        case "ORDER_STATUS":
            return "Buyurtma holati yangilandi"
        case "PRODUCT":
            return "Mahsulot yangiligi"
        case "PROMO":
            return "Aksiya"
        default:
            return "Xabar"
    }
}

// No need to poll for notifications anymore since we have real-time updates
// Remove or comment out the checkOrderStatusUpdates function and its interval

// Declare missing variables (assuming they are defined elsewhere in the project)
let updateNotificationList
let NotificationClient
let showToast
let getTokenFromUrl
let fetchProducts
let loadCategories
let updateCartCount

// Initialize
document.addEventListener("DOMContentLoaded", () => {
    getTokenFromUrl()
    fetchProducts()
    loadCategories()
    loadNotifications() // This will also initialize real-time notifications
    updateCartCount()

    // Remove the polling interval
    // setInterval(checkOrderStatusUpdates, 30000);
})

