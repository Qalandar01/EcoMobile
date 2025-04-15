// Initialize Lucide icons
document.addEventListener('DOMContentLoaded', () => {
    // Initialize Lucide icons
    const lucide = window.lucide;
    lucide.createIcons();

    // Check if dark mode is enabled in localStorage
    if (localStorage.getItem('darkMode') === 'enabled') {
        document.body.classList.add('dark-mode');
        document.getElementById('darkModeToggle').checked = true;
    }

    // Load favorites from localStorage
    loadFavorites();
});

// Navigation
function navigateTo(page, id = null) {
    // Hide all pages
    const pages = document.querySelectorAll('.page');
    pages.forEach(p => p.classList.remove('active'));

    // Show the selected page
    const targetPage = document.getElementById(`${page}-page`);
    if (targetPage) {
        targetPage.classList.add('active');

        // Update checkout title based on step
        if (page === 'checkout') {
            updateCheckoutTitle();
        }

        // Update product details if navigating to product page
        if (page === 'product' && id) {
            updateProductDetails(id);
        }

        // Update favorites page if navigating to favorites
        if (page === 'favorites') {
            renderFavorites();
        }

        // Scroll to top
        window.scrollTo(0, 0);
    }
}

// Onboarding slider
let currentSlide = 0;
const slides = document.querySelectorAll('.onboarding-slide');

function nextSlide() {
    if (currentSlide < slides.length - 1) {
        slides[currentSlide].classList.remove('active');
        currentSlide++;
        slides[currentSlide].classList.add('active');
    } else {
        navigateTo('login');
    }
}

// Auth form toggle
function toggleAuthForm() {
    const loginForm = document.getElementById('login-form');
    const signupForm = document.getElementById('signup-form');

    loginForm.classList.toggle('hidden');
    signupForm.classList.toggle('hidden');
}

// Product quantity
function incrementQuantity() {
    const quantityElement = document.getElementById('quantity');
    let quantity = parseInt(quantityElement.textContent);
    quantityElement.textContent = quantity + 1;
}

function decrementQuantity() {
    const quantityElement = document.getElementById('quantity');
    let quantity = parseInt(quantityElement.textContent);
    if (quantity > 1) {
        quantityElement.textContent = quantity - 1;
    }
}

// Product details update
function updateProductDetails(id) {
    // Mock product data
    const products = {
        1: {
            name: 'Blue Sneakers',
            price: 120.00,
            description: 'Comfortable casual sneakers with breathable material, perfect for everyday wear.',
            image: 'https://via.placeholder.com/400x300'
        },
        2: {
            name: 'Handbag',
            price: 85.00,
            description: 'Stylish handbag with multiple compartments, perfect for any occasion.',
            image: 'https://via.placeholder.com/400x300'
        },
        3: {
            name: 'Headphones',
            price: 199.00,
            description: 'High-quality wireless headphones with noise cancellation technology.',
            image: 'https://via.placeholder.com/400x300'
        },
        4: {
            name: 'White T-Shirt',
            price: 45.00,
            description: 'Comfortable cotton t-shirt, perfect for casual wear.',
            image: 'https://via.placeholder.com/400x300'
        }
    };

    const product = products[id];
    if (product) {
        document.getElementById('product-title').textContent = product.name;
        document.getElementById('product-price').textContent = `$${product.price.toFixed(2)}`;
        document.getElementById('product-description').textContent = product.description;
        document.getElementById('product-main-image').src = product.image;

        // Update favorite button
        const isFavorite = checkIfFavorite(id);
        const favoriteBtn = document.getElementById('product-favorite-btn');
        if (isFavorite) {
            favoriteBtn.classList.add('active');
            favoriteBtn.querySelector('i').classList.add('filled');
        } else {
            favoriteBtn.classList.remove('active');
            favoriteBtn.querySelector('i').classList.remove('filled');
        }
    }
}

// Add to cart
function addToCart() {
    // In a real app, this would add the product to the cart
    // For this demo, we'll just navigate to the cart page
    alert('Product added to cart!');
    navigateTo('cart');
}

// Checkout steps
let checkoutStep = 1;

function nextCheckoutStep() {
    const currentStepElement = document.getElementById(`step-${checkoutStep === 1 ? 'address' : checkoutStep === 2 ? 'payment' : 'summary'}`);
    checkoutStep++;

    if (checkoutStep > 3) {
        navigateTo('success');
        return;
    }

    const nextStepElement = document.getElementById(`step-${checkoutStep === 1 ? 'address' : checkoutStep === 2 ? 'payment' : 'summary'}`);

    currentStepElement.classList.remove('active');
    nextStepElement.classList.add('active');

    updateCheckoutTitle();
}

function updateCheckoutTitle() {
    const titleElement = document.getElementById('checkout-title');
    if (checkoutStep === 1) {
        titleElement.textContent = 'Delivery Address';
    } else if (checkoutStep === 2) {
        titleElement.textContent = 'Payment';
    } else {
        titleElement.textContent = 'Order Summary';
    }
}

// Dark mode toggle
document.addEventListener('DOMContentLoaded', () => {
    const darkModeToggle = document.getElementById('darkModeToggle');
    if (darkModeToggle) {
        darkModeToggle.addEventListener('change', () => {
            if (darkModeToggle.checked) {
                document.body.classList.add('dark-mode');
                localStorage.setItem('darkMode', 'enabled');
            } else {
                document.body.classList.remove('dark-mode');
                localStorage.setItem('darkMode', 'disabled');
            }
        });
    }
});

// Color and size selection
document.addEventListener('click', (e) => {
    // Color selection
    if (e.target.classList.contains('color-option')) {
        const colorOptions = document.querySelectorAll('.color-option');
        colorOptions.forEach(option => option.classList.remove('active'));
        e.target.classList.add('active');
    }

    // Size selection
    if (e.target.classList.contains('size-option')) {
        const sizeOptions = document.querySelectorAll('.size-option');
        sizeOptions.forEach(option => option.classList.remove('active'));
        e.target.classList.add('active');
    }
});

// Favorites functionality
function toggleFavorite(event, productId) {
    event.stopPropagation(); // Prevent navigating to product page

    let favorites = JSON.parse(localStorage.getItem('favorites')) || [];
    const index = favorites.indexOf(productId);

    if (index === -1) {
        // Add to favorites
        favorites.push(productId);
        event.currentTarget.classList.add('active');
        event.currentTarget.querySelector('i').classList.add('filled');
    } else {
        // Remove from favorites
        favorites.splice(index, 1);
        event.currentTarget.classList.remove('active');
        event.currentTarget.querySelector('i').classList.remove('filled');
    }

    localStorage.setItem('favorites', JSON.stringify(favorites));
}

function toggleProductFavorite() {
    const productId = getCurrentProductId();
    const favoriteBtn = document.getElementById('product-favorite-btn');

    let favorites = JSON.parse(localStorage.getItem('favorites')) || [];
    const index = favorites.indexOf(productId);

    if (index === -1) {
        // Add to favorites
        favorites.push(productId);
        favoriteBtn.classList.add('active');
        favoriteBtn.querySelector('i').classList.add('filled');
    } else {
        // Remove from favorites
        favorites.splice(index, 1);
        favoriteBtn.classList.remove('active');
        favoriteBtn.querySelector('i').classList.remove('filled');
    }

    localStorage.setItem('favorites', JSON.stringify(favorites));
}

function getCurrentProductId() {
    // In a real app, this would get the current product ID from the URL or state
    // For this demo, we'll just return a fixed ID
    return 1;
}

function checkIfFavorite(productId) {
    const favorites = JSON.parse(localStorage.getItem('favorites')) || [];
    return favorites.includes(productId);
}

function loadFavorites() {
    const favorites = JSON.parse(localStorage.getItem('favorites')) || [];

    // Update favorite buttons on home page
    document.querySelectorAll('.favorite-btn').forEach(btn => {
        const productCard = btn.closest('.product-card');
        if (productCard) {
            const productId = parseInt(productCard.getAttribute('onclick').match(/\d+/)[0]);
            if (favorites.includes(productId)) {
                btn.classList.add('active');
                btn.querySelector('i').classList.add('filled');
            }
        }
    });
}

function renderFavorites() {
    const favorites = JSON.parse(localStorage.getItem('favorites')) || [];
    const container = document.getElementById('favorites-container');

    if (favorites.length === 0) {
        container.innerHTML = `
            <div class="empty-favorites">
                <p>You don't have any favorites yet.</p>
                <button class="btn-primary" onclick="navigateTo('home')">Start Shopping</button>
            </div>
        `;
        return;
    }

    // Mock product data
    const products = {
        1: {
            name: 'Blue Sneakers',
            price: 120.00,
            image: 'https://via.placeholder.com/150',
            discount: 50
        },
        2: {
            name: 'Handbag',
            price: 85.00,
            image: 'https://via.placeholder.com/150',
            discount: 70
        },
        3: {
            name: 'Headphones',
            price: 199.00,
            image: 'https://via.placeholder.com/150',
            discount: 0
        },
        4: {
            name: 'White T-Shirt',
            price: 45.00,
            image: 'https://via.placeholder.com/150',
            discount: 0
        }
    };

    let html = '';

    favorites.forEach(id => {
        const product = products[id];
        if (product) {
            html += `
                <div class="product-card" onclick="navigateTo('product', ${id})">
                    <div class="product-image">
                        <img src="${product.image}" alt="${product.name}">
                        <button class="favorite-btn active" onclick="toggleFavorite(event, ${id})">
                            <i data-lucide="heart" class="filled"></i>
                        </button>
                        ${product.discount > 0 ? `<div class="discount-badge">${product.discount}% OFF</div>` : ''}
                    </div>
                    <div class="product-info">
                        <h4>${product.name}</h4>
                        <p class="price">$${product.price.toFixed(2)}</p>
                    </div>
                </div>
            `;
        }
    });

    container.innerHTML = html;

    // Re-initialize Lucide icons for the new content
    lucide.createIcons();
}

// Cart functionality
function removeCartItem(id) {
    // In a real app, this would remove the item from the cart
    // For this demo, we'll just show an alert
    alert(`Removed item ${id} from cart`);

    // Remove the item from the DOM for demo purposes
    const cartItem = event.target.closest('.cart-item');
    if (cartItem) {
        cartItem.remove();
    }
}

function updateCartQuantity(id, change) {
    // In a real app, this would update the quantity in the cart
    // For this demo, we'll just update the displayed quantity
    const cartItem = event.target.closest('.cart-item');
    if (cartItem) {
        const quantityElement = cartItem.querySelector('.quantity-selector span');
        let quantity = parseInt(quantityElement.textContent);
        quantity += change;

        if (quantity < 1) quantity = 1;

        quantityElement.textContent = quantity;
    }
}

// Review functionality
function showReviewForm() {
    const modal = document.getElementById('review-modal');
    modal.classList.add('active');
}

function hideReviewForm() {
    const modal = document.getElementById('review-modal');
    modal.classList.remove('active');
}

function setRating(rating) {
    const stars = document.querySelectorAll('.rating-stars .star');
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('filled');
        } else {
            star.classList.remove('filled');
        }
    });
}

function submitReview() {
    // In a real app, this would submit the review to a server
    // For this demo, we'll just show an alert and close the modal
    alert('Thank you for your review!');
    hideReviewForm();

    // Add a mock review to the reviews section
    const reviewsSection = document.querySelector('.reviews-section');
    const reviewItem = document.createElement('div');
    reviewItem.className = 'review-item';
    reviewItem.innerHTML = `
        <div class="review-header">
            <div class="reviewer-info">
                <img src="https://via.placeholder.com/40" alt="User" class="reviewer-image">
                <div>
                    <h4>You</h4>
                    <div class="rating small">
                        <i data-lucide="star" class="star filled"></i>
                        <i data-lucide="star" class="star filled"></i>
                        <i data-lucide="star" class="star filled"></i>
                        <i data-lucide="star" class="star filled"></i>
                        <i data-lucide="star" class="star filled"></i>
                    </div>
                </div>
            </div>
            <span class="review-date">Just now</span>
        </div>
        <p class="review-text">Great product! Exactly what I was looking for.</p>
    `;

    // Insert the new review at the top of the reviews section
    const firstReview = reviewsSection.querySelector('.review-item');
    reviewsSection.insertBefore(reviewItem, firstReview);

    // Re-initialize Lucide icons for the new content
    lucide.createIcons();
}

function showAllReviews() {
    // In a real app, this would navigate to a reviews page
    // For this demo, we'll just show an alert
    alert('Showing all reviews');
}

// Chat functionality
function toggleChat() {
    const chatContainer = document.getElementById('chat-container');
    chatContainer.classList.toggle('active');
}

function sendChatMessage() {
    const inputField = document.getElementById('chat-input-field');
    const message = inputField.value.trim();

    if (message) {
        // Add user message
        addChatMessage(message, 'user');

        // Clear input field
        inputField.value = '';

        // Simulate response after a short delay
        setTimeout(() => {
            const responses = [
                "Thank you for your message. How can I help you today?",
                "I'll check that for you right away.",
                "Is there anything else you'd like to know about our products?",
                "We typically ship orders within 1-2 business days.",
                "Feel free to ask if you have any other questions!"
            ];
            const randomResponse = responses[Math.floor(Math.random() * responses.length)];
            addChatMessage(randomResponse, 'support');
        }, 1000);
    }
}

function addChatMessage(text, type) {
    const messagesContainer = document.getElementById('chat-messages');
    const messageElement = document.createElement('div');
    messageElement.className = `message ${type}`;

    const now = new Date();
    const timeString = `${now.getHours()}:${now.getMinutes().toString().padStart(2, '0')}`;

    messageElement.innerHTML = `
        <div class="message-content">
            <p>${text}</p>
            <span class="message-time">${timeString}</span>
        </div>
    `;

    messagesContainer.appendChild(messageElement);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// Initialize the app
document.addEventListener('DOMContentLoaded', () => {
    // Set up event listener for chat input
    const chatInput = document.getElementById('chat-input-field');
    if (chatInput) {
        chatInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                sendChatMessage();
            }
        });
    }
});