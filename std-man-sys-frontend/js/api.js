const API_BASE_URL = 'http://localhost:8080/api';

let authToken = localStorage.getItem('token');

function setAuthToken(token) {
    authToken = token;
    if (token) {
        localStorage.setItem('token', token);
    } else {
        localStorage.removeItem('token');
    }
}

function getHeaders() {
    const headers = {
        'Content-Type': 'application/json'
    };
    if (authToken) {
        headers['Authorization'] = `Bearer ${authToken}`;
    }
    return headers;
}

async function apiRequest(endpoint, method, body = null) {
    const options = {
        method: method,
        headers: getHeaders()
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

    if (response.status === 401 || response.status === 403) {
        localStorage.removeItem('token');
        window.location.href = '/login.html';
        return null;
    }

    return response;
}

async function register(userData) {
    const response = await apiRequest('/auth/register', 'POST', userData);
    if (response && response.ok) {
        const data = await response.json();
        setAuthToken(data.token);
        return { success: true, data };
    }
    if (response && response.status === 409) {
        const error = await response.json();
        return { success: false, message: error.message || 'Username already exists' };
    }
    return { success: false, message: 'Registration failed' };
}

async function login(credentials) {
    const response = await apiRequest('/auth/login', 'POST', credentials);
    if (response && response.ok) {
        const data = await response.json();
        setAuthToken(data.token);
        return { success: true, data };
    }
    if (response && response.status === 401) {
        return { success: false, message: 'Invalid username or password' };
    }
    return { success: false, message: 'Login failed' };
}

async function getStudents() {
    const response = await apiRequest('/students', 'GET');
    if (response && response.ok) {
        return await response.json();
    }
    return [];
}

async function getStudentById(id) {
    const response = await apiRequest(`/students/${id}`, 'GET');
    if (response && response.ok) {
        return await response.json();
    }
    return null;
}

async function createStudent(studentData) {
    const response = await apiRequest('/students', 'POST', studentData);
    if (response && response.ok) {
        return { success: true, data: await response.json() };
    }
    if (response && response.status === 409) {
        const error = await response.json();
        return { success: false, message: error.message || 'Email already exists' };
    }
    return { success: false, message: 'Failed to save student' };
}

async function updateStudentById(id, studentData) {
    const response = await apiRequest(`/students/${id}`, 'PUT', studentData);
    if (response && response.ok) {
        return { success: true, data: await response.json() };
    }
    if (response && response.status === 409) {
        const error = await response.json();
        return { success: false, message: error.message || 'Email already exists' };
    }
    if (response && response.status === 404) {
        return { success: false, message: 'Student not found' };
    }
    return { success: false, message: 'Failed to update student' };
}

async function deleteStudentById(id) {
    const response = await apiRequest(`/students/${id}`, 'DELETE');
    if (response && response.ok) {
        return { success: true };
    }
    if (response && response.status === 404) {
        return { success: false, message: 'Student not found' };
    }
    return { success: false, message: 'Failed to delete student' };
}

async function searchStudentsByName(name) {
    const response = await apiRequest(`/students/search?name=${encodeURIComponent(name)}`, 'GET');
    if (response && response.ok) {
        return await response.json();
    }
    return [];
}

async function getStudentsByCourse(course) {
    const response = await apiRequest(`/students/course/${encodeURIComponent(course)}`, 'GET');
    if (response && response.ok) {
        return await response.json();
    }
    return [];
}

function logout() {
    localStorage.removeItem('token');
    window.location.href = '/login.html';
}