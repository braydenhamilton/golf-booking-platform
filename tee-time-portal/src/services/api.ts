import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add a request interceptor to include the auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor to handle common errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (credentials: { username: string; password: string }) =>
    api.post('/login', credentials),
  register: (userData: {
    username: string;
    email: string;
    password: string;
    golfNZMemberId: string;
    golfNZPassword: string;
  }) => api.post('/register', userData),
  logout: () => api.post('/logout'),
};

export const bookingAPI = {
  makeBooking: (bookingData: {
    date: string;
    time: string;
    course: string;
    players: number;
  }) => api.post('/makeBooking', bookingData),
  getBookings: () => api.get('/getBookings'),
  modifyBooking: (bookingData: {
    id: string;
    date: string;
    time: string;
    course: string;
    players: number;
  }) => api.put('/modifyBooking', bookingData),
  deleteBooking: (bookingId: string) =>
    api.delete(`/deleteBooking/${bookingId}`),
};

export const schedulerAPI = {
  scheduleBooking: (bookingData: {
    date: string;
    time: string;
    course: string;
    players: number;
  }) => api.post('/scheduleBooking', bookingData),
  getScheduledBookings: () => api.get('/getScheduledBookings'),
  cancelScheduledBooking: (bookingId: string) =>
    api.delete(`/cancelScheduledBooking/${bookingId}`),
};

export default api; 