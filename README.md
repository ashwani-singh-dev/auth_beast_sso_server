# AuthBeast SSO Authentication Server

**AuthBeast** is a robust authentication server built with Spring Boot. It provides secure Single Sign-On (SSO) using PKCE (Proof Key for Code Exchange), multi-factor authentication (MFA) via email OTP, secure password management, and session management. The server also supports API key verification to ensure that only trusted applications can integrate with it.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Key Components](#key-components)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [Usage](#usage)
  - [SSO with PKCE](#sso-with-pkce)
  - [API Key Verification](#api-key-verification)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Features

- **Single Sign-On (SSO)**: Generate and validate SSO tokens with support for multiple concurrent logins.
- **PKCE**: Secure authorization flow to prevent interception attacks.
- **Multi-factor Authentication (MFA)**: Email-based OTP for additional security.
- **Secure Password Management**: End-to-end password reset and management flow.
- **Encryption**: RSA key pair-based encryption and JWE token support.
- **Session Management**: Maintain and track user sessions securely.
- **API Key Authentication**: Protect endpoints by requiring a valid API key from registered applications.

## Tech Stack

- **Java**
- **Spring Boot**
- **Spring Security**
- **Thymeleaf**
- **Redis**
- **JOSE (Java Object Signing and Encryption)**
- **RSA Encryption**

## Key Components

- **SSO Token Service**: Manages generation and validation of access and refresh tokens.
- **OTP Helper**: Handles OTP generation, validation, and expiry for MFA.
- **Custom JWE Util**: Manages JWT token encryption and decryption.
- **Session Manager**: Tracks and manages user sessions.
- **RSA Util**: Generates and stores RSA key pairs.
- **API Key Validator**: Verifies that incoming requests contain valid API keys.

## Getting Started

### Prerequisites

- Java 11 or later
- Maven 3.6+
- Redis Server (for session management)
- An IDE of your choice (e.g., IntelliJ IDEA, Eclipse)

### Installation
Note: This repository is a demonstration of AuthBeast's architecture and code structure. Some configuration files and dependencies are intentionally omitted for security reasons. Full implementation details will be shared upon request.
