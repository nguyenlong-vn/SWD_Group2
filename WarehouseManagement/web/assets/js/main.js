/**
* Template Name: Invent
* Template URL: https://bootstrapmade.com/invent-bootstrap-business-template/
* Updated: May 12 2025 with Bootstrap v5.3.6
* Author: BootstrapMade.com
* License: https://bootstrapmade.com/license/
*/

(function() {
  "use strict";

  /**
   * Apply .scrolled class to the body as the page is scrolled down
   */
function toggleScrolled() {
  const selectBody = document.querySelector('body');
  const selectHeader = document.querySelector('#header');

  if (!selectHeader) return;   // ✅ FIX QUAN TRỌNG

  if (
    !selectHeader.classList.contains('scroll-up-sticky') &&
    !selectHeader.classList.contains('sticky-top') &&
    !selectHeader.classList.contains('fixed-top')
  ) return;

  window.scrollY > 100
    ? selectBody.classList.add('scrolled')
    : selectBody.classList.remove('scrolled');
}

  document.addEventListener('scroll', toggleScrolled);
  window.addEventListener('load', toggleScrolled);

  /**
   * Mobile nav toggle
   */
  const mobileNavToggleBtn = document.querySelector('.mobile-nav-toggle');

  function mobileNavToogle() {
    document.querySelector('body').classList.toggle('mobile-nav-active');
    mobileNavToggleBtn.classList.toggle('bi-list');
    mobileNavToggleBtn.classList.toggle('bi-x');
  }
  if (mobileNavToggleBtn) {
    mobileNavToggleBtn.addEventListener('click', mobileNavToogle);
  }

  /**
   * Hide mobile nav on same-page/hash links
   */
  document.querySelectorAll('#navmenu a').forEach(navmenu => {
    navmenu.addEventListener('click', () => {
      if (document.querySelector('.mobile-nav-active')) {
        mobileNavToogle();
      }
    });

  });

  /**
   * Toggle mobile nav dropdowns
   */
  document.querySelectorAll('.navmenu .toggle-dropdown').forEach(navmenu => {
    navmenu.addEventListener('click', function(e) {
      e.preventDefault();
      this.parentNode.classList.toggle('active');
      this.parentNode.nextElementSibling.classList.toggle('dropdown-active');
      e.stopImmediatePropagation();
    });
  });

  /**
   * Preloader
   */
  const preloader = document.querySelector('#preloader');
  if (preloader) {
    window.addEventListener('load', () => {
      preloader.remove();
    });
  }

  /**
   * Scroll top button
   */
  let scrollTop = document.querySelector('.scroll-top');

  function toggleScrollTop() {
    if (scrollTop) {
      window.scrollY > 100 ? scrollTop.classList.add('active') : scrollTop.classList.remove('active');
    }
  }
  if (scrollTop) {
    scrollTop.addEventListener('click', (e) => {
      e.preventDefault();
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    });

    window.addEventListener('load', toggleScrollTop);
    document.addEventListener('scroll', toggleScrollTop);
  }

  /**
   * Animation on scroll function and init
   */
  function aosInit() {
    AOS.init({
      duration: 600,
      easing: 'ease-in-out',
      once: true,
      mirror: false
    });
  }
  window.addEventListener('load', aosInit);

  /**
   * Initiate glightbox
   */
  const glightbox = GLightbox({
    selector: '.glightbox'
  });

  /**
   * Init isotope layout and filters
   */
  document.querySelectorAll('.isotope-layout').forEach(function(isotopeItem) {
    let layout = isotopeItem.getAttribute('data-layout') ?? 'masonry';
    let filter = isotopeItem.getAttribute('data-default-filter') ?? '*';
    let sort = isotopeItem.getAttribute('data-sort') ?? 'original-order';

    let initIsotope;
    imagesLoaded(isotopeItem.querySelector('.isotope-container'), function() {
      initIsotope = new Isotope(isotopeItem.querySelector('.isotope-container'), {
        itemSelector: '.isotope-item',
        layoutMode: layout,
        filter: filter,
        sortBy: sort
      });
    });

    isotopeItem.querySelectorAll('.isotope-filters li').forEach(function(filters) {
      filters.addEventListener('click', function() {
        isotopeItem.querySelector('.isotope-filters .filter-active').classList.remove('filter-active');
        this.classList.add('filter-active');
        initIsotope.arrange({
          filter: this.getAttribute('data-filter')
        });
        if (typeof aosInit === 'function') {
          aosInit();
        }
      }, false);
    });

  });

  /**
   * Frequently Asked Questions Toggle
   */
  document.querySelectorAll('.faq-item h3, .faq-item .faq-toggle, .faq-item .faq-header').forEach((faqItem) => {
    faqItem.addEventListener('click', () => {
      faqItem.parentNode.classList.toggle('faq-active');
    });
  });

  /**
   * Init swiper sliders
   */
  function initSwiper() {
    document.querySelectorAll(".init-swiper").forEach(function(swiperElement) {
      let config = JSON.parse(
        swiperElement.querySelector(".swiper-config").innerHTML.trim()
      );

      if (swiperElement.classList.contains("swiper-tab")) {
        initSwiperWithCustomPagination(swiperElement, config);
      } else {
        new Swiper(swiperElement, config);
      }
    });
  }

  window.addEventListener("load", initSwiper);

  /**
   * Correct scrolling position upon page load for URLs containing hash links.
   */
  window.addEventListener('load', function(e) {
    if (window.location.hash) {
      if (document.querySelector(window.location.hash)) {
        setTimeout(() => {
          let section = document.querySelector(window.location.hash);
          let scrollMarginTop = getComputedStyle(section).scrollMarginTop;
          window.scrollTo({
            top: section.offsetTop - parseInt(scrollMarginTop),
            behavior: 'smooth'
          });
        }, 100);
      }
    }
  });

  /**
   * Navmenu Scrollspy
   */
  let navmenulinks = document.querySelectorAll('.navmenu a');

  function navmenuScrollspy() {
    navmenulinks.forEach(navmenulink => {
      if (!navmenulink.hash) return;
      let section = document.querySelector(navmenulink.hash);
      if (!section) return;
      let position = window.scrollY + 200;
      if (position >= section.offsetTop && position <= (section.offsetTop + section.offsetHeight)) {
        document.querySelectorAll('.navmenu a.active').forEach(link => link.classList.remove('active'));
        navmenulink.classList.add('active');
      } else {
        navmenulink.classList.remove('active');
      }
    })
  }
  window.addEventListener('load', navmenuScrollspy);
  document.addEventListener('scroll', navmenuScrollspy);

})();

// Corporate Login Form JavaScript
class CorporateLoginForm {
    constructor() {
        this.form = document.getElementById('loginForm');
        if (!this.form) return; // Exit if form doesn't exist
        
        // Support both 'email' and 'username' input fields
        this.emailInput = document.getElementById('email') || document.getElementById('username');
        this.passwordInput = document.getElementById('password');
        this.passwordToggle = document.getElementById('passwordToggle');
        this.submitButton = this.form.querySelector('.login-btn');
        this.successMessage = document.getElementById('successMessage');
        this.ssoButtons = document.querySelectorAll('.sso-btn');
        
        this.init();
    }
    
    init() {
        this.checkInputValues(); // Check for pre-filled values
        this.bindEvents();
        this.setupPasswordToggle();
        this.setupSSOButtons();
    }
    
    checkInputValues() {
        // Check if inputs have values on page load and update label position
        if (this.emailInput && this.emailInput.value.trim()) {
            this.emailInput.classList.add('has-value');
        }
        if (this.passwordInput && this.passwordInput.value.trim()) {
            this.passwordInput.classList.add('has-value');
        }
    }
    
    bindEvents() {
        if (!this.emailInput || !this.passwordInput) return;
        
        this.form.addEventListener('submit', (e) => this.handleSubmit(e));
        this.emailInput.addEventListener('blur', () => this.validateEmail());
        this.passwordInput.addEventListener('blur', () => this.validatePassword());
        this.emailInput.addEventListener('input', () => {
            this.clearError('email');
            // Update label position when typing
            if (this.emailInput.value.trim()) {
                this.emailInput.classList.add('has-value');
            } else {
                this.emailInput.classList.remove('has-value');
            }
        });
        this.passwordInput.addEventListener('input', () => {
            this.clearError('password');
            // Update label position when typing
            if (this.passwordInput.value.trim()) {
                this.passwordInput.classList.add('has-value');
            } else {
                this.passwordInput.classList.remove('has-value');
            }
        });
    }
    
    setupPasswordToggle() {
        if (!this.passwordToggle || !this.passwordInput) return; // Exit if elements don't exist
        
        this.passwordToggle.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            
            const type = this.passwordInput.type === 'password' ? 'text' : 'password';
            this.passwordInput.type = type;
            
            const icon = this.passwordToggle.querySelector('.toggle-icon');
            if (icon) {
                icon.classList.toggle('show-password', type === 'text');
            }
        });
    }
    
    setupSSOButtons() {
        this.ssoButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                const provider = button.classList.contains('azure-btn') ? 'Azure AD' : 'Okta';
                this.handleSSOLogin(provider);
            });
        });
    }
    
    validateEmail() {
        if (!this.emailInput) return true; // Skip validation if input doesn't exist
        
        const email = this.emailInput.value.trim();
        const inputId = this.emailInput.id;
        
        // If it's username field, just check if not empty
        if (inputId === 'username') {
            if (!email) {
                this.showError('username', 'Username is required');
                return false;
            }
            this.clearError('username');
            return true;
        }
        
        // If it's email field, validate email format
        const businessEmailRegex = /^[^\s@]+@[^\s@]+\.(com|org|net|edu|gov|mil)$/i;
        
        if (!email) {
            this.showError('email', 'Business email is required');
            return false;
        }
        
        if (!businessEmailRegex.test(email)) {
            this.showError('email', 'Please enter a valid business email address');
            return false;
        }
        
        // Check for common personal email domains
        const personalDomains = ['gmail.com', 'yahoo.com', 'hotmail.com', 'outlook.com'];
        const domain = email.split('@')[1]?.toLowerCase();
        if (personalDomains.includes(domain)) {
            this.showError('email', 'Please use your business email address');
            return false;
        }
        
        this.clearError('email');
        return true;
    }
    
    validatePassword() {
        const password = this.passwordInput.value;
        
        if (!password) {
            this.showError('password', 'Password is required');
            return false;
        }
        
        if (password.length < 8) {
            this.showError('password', 'Password must be at least 8 characters');
            return false;
        }
        
        // Corporate password strength check
        const hasUpperCase = /[A-Z]/.test(password);
        const hasLowerCase = /[a-z]/.test(password);
        const hasNumbers = /\d/.test(password);
        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
        
        if (!hasUpperCase || !hasLowerCase || !hasNumbers || !hasSpecialChar) {
            this.showError('password', 'Password must contain uppercase, lowercase, number, and special character');
            return false;
        }
        
        this.clearError('password');
        return true;
    }
    
    showError(field, message) {
        const fieldElement = document.getElementById(field);
        if (!fieldElement) return;
        
        const formGroup = fieldElement.closest('.form-group');
        const errorElement = document.getElementById(`${field}Error`) || formGroup.querySelector('.error-message');
        
        if (formGroup) formGroup.classList.add('error');
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.classList.add('show');
        }
    }
    
    clearError(field) {
        const fieldElement = document.getElementById(field);
        if (!fieldElement) return;
        
        const formGroup = fieldElement.closest('.form-group');
        const errorElement = document.getElementById(`${field}Error`) || formGroup.querySelector('.error-message');
        
        if (formGroup) formGroup.classList.remove('error');
        if (errorElement) {
            errorElement.classList.remove('show');
            setTimeout(() => {
                errorElement.textContent = '';
            }, 300);
        }
    }
    
    async handleSubmit(e) {
        e.preventDefault();
        
        // If form has action, let it submit normally (server-side validation)
        if (this.form.action && this.form.action !== '#' && !this.form.action.includes('javascript:')) {
            // Only validate client-side if no server action
            const isEmailValid = this.validateEmail();
            const isPasswordValid = this.validatePassword();
            
            if (!isEmailValid || !isPasswordValid) {
                return;
            }
        }
        
        // If form has server action, submit normally
        // Otherwise show loading state
        if (!this.form.action || this.form.action === '#' || this.form.action.includes('javascript:')) {
            this.setLoading(true);
            
            try {
                // Simulate corporate authentication with MFA check
                await new Promise(resolve => setTimeout(resolve, 2000));
                
                // Show success state
                this.showSuccess();
            } catch (error) {
                this.showError('password', 'Authentication failed. Please contact IT support.');
            } finally {
                this.setLoading(false);
            }
        }
    }
    
    async handleSSOLogin(provider) {
        console.log(`Initiating SSO login with ${provider}...`);
        
        // Simulate SSO redirect
        const ssoButton = document.querySelector(`.${provider.toLowerCase().replace(' ', '-')}-btn`);
        ssoButton.style.opacity = '0.6';
        ssoButton.style.pointerEvents = 'none';
        
        try {
            await new Promise(resolve => setTimeout(resolve, 1500));
            console.log(`Redirecting to ${provider} authentication...`);
            // window.location.href = `/auth/${provider.toLowerCase()}`;
        } catch (error) {
            console.error(`SSO authentication failed: ${error.message}`);
        } finally {
            ssoButton.style.opacity = '1';
            ssoButton.style.pointerEvents = 'auto';
        }
    }
    
    setLoading(loading) {
        this.submitButton.classList.toggle('loading', loading);
        this.submitButton.disabled = loading;
        
        // Disable SSO buttons during login
        this.ssoButtons.forEach(button => {
            button.style.pointerEvents = loading ? 'none' : 'auto';
            button.style.opacity = loading ? '0.6' : '1';
        });
    }
    
    showSuccess() {
        this.form.style.display = 'none';
        document.querySelector('.sso-options').style.display = 'none';
        document.querySelector('.footer-links').style.display = 'none';
        this.successMessage.classList.add('show');
        
        // Simulate redirect after 3 seconds (corporate systems are slower)
        setTimeout(() => {
            console.log('Redirecting to corporate workspace...');
            // window.location.href = '/workspace';
        }, 3000);
    }
}

// Initialize the form when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new CorporateLoginForm();
});




