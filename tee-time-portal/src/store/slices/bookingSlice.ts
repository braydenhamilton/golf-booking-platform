import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface Booking {
  id: string;
  date: string;
  time: string;
  course: string;
  players: number;
  status: 'pending' | 'confirmed' | 'cancelled';
}

interface BookingState {
  bookings: Booking[];
  loading: boolean;
  error: string | null;
}

const initialState: BookingState = {
  bookings: [],
  loading: false,
  error: null,
};

const bookingSlice = createSlice({
  name: 'booking',
  initialState,
  reducers: {
    fetchBookingsStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    fetchBookingsSuccess: (state, action: PayloadAction<Booking[]>) => {
      state.loading = false;
      state.bookings = action.payload;
      state.error = null;
    },
    fetchBookingsFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    addBooking: (state, action: PayloadAction<Booking>) => {
      state.bookings.push(action.payload);
    },
    updateBooking: (state, action: PayloadAction<Booking>) => {
      const index = state.bookings.findIndex(booking => booking.id === action.payload.id);
      if (index !== -1) {
        state.bookings[index] = action.payload;
      }
    },
    deleteBooking: (state, action: PayloadAction<string>) => {
      state.bookings = state.bookings.filter(booking => booking.id !== action.payload);
    },
  },
});

export const {
  fetchBookingsStart,
  fetchBookingsSuccess,
  fetchBookingsFailure,
  addBooking,
  updateBooking,
  deleteBooking,
} = bookingSlice.actions;

export default bookingSlice.reducer; 