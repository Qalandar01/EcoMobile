// Notification client for Server-Sent Events
class NotificationClient {
    constructor(userId, token) {
        this.userId = userId
        this.token = token
        this.eventSource = null
        this.onNotificationCallback = null
        this.reconnectAttempts = 0
        this.maxReconnectAttempts = 5
        this.reconnectDelay = 3000 // 3 seconds
    }

    // Connect to notification stream
    connect() {
        if (!this.userId || !this.token) {
            console.error("User ID and token are required to connect to notifications")
            return
        }

        // Close existing connection if any
        this.disconnect()

        try {
            // Create new EventSource connection
            this.eventSource = new EventSource(`http://localhost:8080/api/notification-events/subscribe/${this.userId}`)

            // Connection opened
            this.eventSource.onopen = () => {
                console.log("Notification connection established")
                this.reconnectAttempts = 0
            }

            // Handle messages
            this.eventSource.addEventListener("NOTIFICATION", (event) => {
                try {
                    const notification = JSON.parse(event.data)
                    if (this.onNotificationCallback) {
                        this.onNotificationCallback(notification)
                    }
                } catch (error) {
                    console.error("Error parsing notification:", error)
                }
            })

            // Handle errors
            this.eventSource.onerror = (error) => {
                console.error("Notification connection error:", error)
                this.disconnect()

                // Try to reconnect
                if (this.reconnectAttempts < this.maxReconnectAttempts) {
                    this.reconnectAttempts++
                    console.log(`Reconnecting in ${this.reconnectDelay / 1000} seconds... (Attempt ${this.reconnectAttempts})`)
                    setTimeout(() => this.connect(), this.reconnectDelay)
                }
            }
        } catch (error) {
            console.error("Failed to connect to notification stream:", error)
        }
    }

    // Disconnect from notification stream
    disconnect() {
        if (this.eventSource) {
            this.eventSource.close()
            this.eventSource = null
        }
    }

    // Set callback for new notifications
    onNotification(callback) {
        this.onNotificationCallback = callback
    }
}

