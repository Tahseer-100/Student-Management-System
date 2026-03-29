let currentEditId = null;

function showLoading() {
    const grid = document.getElementById('studentGrid');
    grid.innerHTML = '<div class="loading-spinner"><div class="spinner"></div><p>Loading students...</p></div>';
}

function hideLoading() {
}

async function loadStudents() {
    showLoading();
    try {
        const students = await getStudents();
        displayStudents(students);
    } catch (error) {
        console.error('Error loading students:', error);
        document.getElementById('studentGrid').innerHTML = '<p style="text-align:center; color:white;">Error loading students. Please refresh.</p>';
    }
}

function displayStudents(students) {
    const grid = document.getElementById('studentGrid');

    if (!students || students.length === 0) {
        grid.innerHTML = '<p style="text-align:center; color:white;">No students found. Click "Add Student" to create one.</p>';
        return;
    }

    grid.innerHTML = students.map(student => `
        <div class="student-card">
            <h3>${escapeHtml(student.firstName)} ${escapeHtml(student.lastName)}</h3>
            <p>📧 ${escapeHtml(student.email)}</p>
            <p>📚 ${escapeHtml(student.course)}</p>
            <div class="marks">⭐ ${student.marks}%</div>
            <p>📅 ${new Date(student.createdAt).toLocaleDateString()}</p>
            <div class="card-actions">
                <button class="edit-btn" onclick="showEditModal(${student.id})">Edit</button>
                <button class="delete-btn" onclick="confirmDelete(${student.id})">Delete</button>
            </div>
        </div>
    `).join('');
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}

async function showAddModal() {
    currentEditId = null;
    document.getElementById('modalTitle').textContent = 'Add Student';
    document.getElementById('studentForm').reset();
    document.getElementById('studentModal').style.display = 'block';
}

async function showEditModal(id) {
    try {
        const student = await getStudentById(id);
        if (student) {
            currentEditId = id;
            document.getElementById('modalTitle').textContent = 'Edit Student';
            document.getElementById('firstName').value = student.firstName;
            document.getElementById('lastName').value = student.lastName;
            document.getElementById('email').value = student.email;
            document.getElementById('course').value = student.course;
            document.getElementById('marks').value = student.marks;
            document.getElementById('studentModal').style.display = 'block';
        } else {
            alert('Student not found');
        }
    } catch (error) {
        console.error('Error loading student:', error);
        alert('Error loading student details');
    }
}

async function confirmDelete(id) {
    if (confirm('Are you sure you want to delete this student? This action cannot be undone.')) {
        await deleteStudentRecord(id);
    }
}

async function deleteStudentRecord(id) {
    const saveButton = document.querySelector('#studentModal .btn');
    try {
        const result = await deleteStudentById(id);
        if (result.success) {
            loadStudents();
        } else {
            alert(result.message || 'Failed to delete student');
        }
    } catch (error) {
        console.error('Error deleting student:', error);
        alert('Error deleting student. Please try again.');
    }
}

async function searchStudents() {
    const name = document.getElementById('searchInput').value.trim();
    if (name === '') {
        loadStudents();
    } else {
        showLoading();
        try {
            const students = await searchStudentsByName(name);
            displayStudents(students);
        } catch (error) {
            console.error('Error searching students:', error);
            document.getElementById('studentGrid').innerHTML = '<p style="text-align:center; color:white;">Error searching. Please try again.</p>';
        }
    }
}

async function filterByCourse() {
    const course = document.getElementById('courseFilter').value;
    if (course === '') {
        loadStudents();
    } else {
        showLoading();
        try {
            const students = await getStudentsByCourse(course);
            displayStudents(students);
        } catch (error) {
            console.error('Error filtering students:', error);
            document.getElementById('studentGrid').innerHTML = '<p style="text-align:center; color:white;">Error filtering. Please try again.</p>';
        }
    }
}

function closeModal() {
    document.getElementById('studentModal').style.display = 'none';
    currentEditId = null;
}

document.getElementById('studentForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const saveButton = e.target.querySelector('button[type="submit"]');
    const originalText = saveButton.textContent;
    saveButton.textContent = 'Saving...';
    saveButton.disabled = true;

    const studentData = {
        firstName: document.getElementById('firstName').value.trim(),
        lastName: document.getElementById('lastName').value.trim(),
        email: document.getElementById('email').value.trim(),
        course: document.getElementById('course').value.trim(),
        marks: parseInt(document.getElementById('marks').value)
    };

    if (!studentData.firstName || !studentData.lastName || !studentData.email || !studentData.course) {
        alert('Please fill all fields');
        saveButton.textContent = originalText;
        saveButton.disabled = false;
        return;
    }

    if (studentData.marks < 0 || studentData.marks > 100) {
        alert('Marks must be between 0 and 100');
        saveButton.textContent = originalText;
        saveButton.disabled = false;
        return;
    }

    try {
        let result;
        if (currentEditId) {
            result = await updateStudentById(currentEditId, studentData);
        } else {
            result = await createStudent(studentData);
        }

        if (result.success) {
            closeModal();
            loadStudents();
        } else {
            alert(result.message || 'Failed to save student');
        }
    } catch (error) {
        console.error('Error saving student:', error);
        alert('Error saving student. Please try again.');
    } finally {
        saveButton.textContent = originalText;
        saveButton.disabled = false;
    }
});

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        document.getElementById('username').textContent = payload.sub;
    } catch (e) {
        console.error('Invalid token');
        localStorage.removeItem('token');
        window.location.href = '/login.html';
        return;
    }

    loadStudents();
});