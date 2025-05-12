import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';

interface ScheduledBooking {
  id: string;
  date: string;
  time: string;
  course: string;
  players: number;
  status: 'scheduled' | 'completed' | 'failed';
  attempts: number;
  lastAttempt: string | null;
}

interface SchedulerState {
  scheduledBookings: ScheduledBooking[];
  loading: boolean;
  error: string | null;
}

const initialState: SchedulerState = {
  scheduledBookings: [],
  loading: false,
  error: null,
};

const schedulerSlice = createSlice({
  name: 'scheduler',
  initialState,
  reducers: {
    fetchScheduledBookingsStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    fetchScheduledBookingsSuccess: (state, action: PayloadAction<ScheduledBooking[]>) => {
      state.loading = false;
      state.scheduledBookings = action.payload;
      state.error = null;
    },
    fetchScheduledBookingsFailure: (state, action: PayloadAction<string>) => {
      state.loading = false;
      state.error = action.payload;
    },
    addScheduledBooking: (state, action: PayloadAction<ScheduledBooking>) => {
      state.scheduledBookings.push(action.payload);
    },
    updateScheduledBooking: (state, action: PayloadAction<ScheduledBooking>) => {
      const index = state.scheduledBookings.findIndex(booking => booking.id === action.payload.id);
      if (index !== -1) {
        state.scheduledBookings[index] = action.payload;
      }
    },
    deleteScheduledBooking: (state, action: PayloadAction<string>) => {
      state.scheduledBookings = state.scheduledBookings.filter(booking => booking.id !== action.payload);
    },
  },
});

export const {
  fetchScheduledBookingsStart,
  fetchScheduledBookingsSuccess,
  fetchScheduledBookingsFailure,
  addScheduledBooking,
  updateScheduledBooking,
  deleteScheduledBooking,
} = schedulerSlice.actions;

export default schedulerSlice.reducer; 