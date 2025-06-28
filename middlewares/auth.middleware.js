const jwt = require("jsonwebtoken");
const mongoose = require("mongoose");

exports.authenticate = (req, res, next) => {
  let token;

  // Debug log để xem request
  console.log('=== AUTH DEBUG ===');
  console.log('Headers:', req.headers);
  console.log('Cookies:', req.cookies);
  console.log('==================');

  // Kiểm tra token từ nhiều nguồn
  if (req.headers.authorization) {
    const authHeader = req.headers.authorization;
    token = authHeader.startsWith('Bearer ') 
      ? authHeader.slice(7) 
      : authHeader;
  } else if (req.headers['access-token']) {
    token = req.headers['access-token'];
  } else if (req.headers['x-access-token']) {
    token = req.headers['x-access-token'];
  } else if (req.cookies && req.cookies.accessToken) {
    token = req.cookies.accessToken;
  }

  if (!token) {
    console.log('No token found in request');
    return res.status(401).json({ 
      message: "Missing authorization token",
      debug: {
        hasAuthHeader: !!req.headers.authorization,
        hasAccessToken: !!req.headers['access-token'],
        hasCookies: !!req.cookies,
        availableHeaders: Object.keys(req.headers)
      }
    });
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    if (!decoded.userId || !mongoose.Types.ObjectId.isValid(decoded.userId)) {
      console.log('Invalid userId in token:', decoded);
      return res.status(403).json({ 
        message: "Invalid userId in token",
        debug: { decoded }
      });
    }

    req.userId = decoded.userId; // Sử dụng userId thay vì id
    req.user = decoded;
    req.role = decoded.role || 'user'; // Gán role mặc định nếu không có
    console.log('Token verified successfully for user:', decoded.userId);
    next();
  } catch (error) {
    console.log('Token verification failed:', error.message);
    return res.status(403).json({ 
      message: "Invalid token",
      error: error.message 
    });
  }
};

exports.authorizeRoles = (...allowedRoles) => {
  return (req, res, next) => {
    if (!req.user || !allowedRoles.includes(req.user.role)) {
      return res.status(403).json({ message: "Forbidden" });
    }
    next();
  };
};