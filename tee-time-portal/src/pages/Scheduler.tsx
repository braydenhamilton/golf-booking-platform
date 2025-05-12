import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../store';
import { addScheduledBooking } from '../store/slices/schedulerSlice';

const Scheduler = () => {
  const dispatch = useDispatch();
  const { isAuthenticated } = useSelector((state: RootState) => state.auth);
  const { scheduledBookings } = useSelector((state: RootState) => state.scheduler);
  const [formData, setFormData] = useState({
    date: '',
    time: '',
    course: '',
    players: 1,
  });
  const [error, setError] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!isAuthenticated) {
      setError('Please log in to schedule a booking');
      return;
    }

    try {
      // TODO: Replace with actual API call
      const response = await fetch('/api/scheduleBooking', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        throw new Error('Scheduling failed');
      }

      const data = await response.json();
      dispatch(addScheduledBooking(data));
      setFormData({
        date: '',
        time: '',
        course: '',
        players: 1,
      });
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'An error occurred';
      setError(errorMessage);
    }
  };

  return (
    <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <div className="max-w-3xl mx-auto">
          <h1 className="text-3xl font-bold text-gray-900">Schedule Automated Booking</h1>
          <form onSubmit={handleSubmit} className="mt-8 space-y-6">
            {error && (
              <div className="rounded-md bg-red-50 p-4">
                <div className="text-sm text-red-700">{error}</div>
              </div>
            )}
            <div className="space-y-4">
              <div>
                <label htmlFor="date" className="block text-sm font-medium text-gray-700">
                  Date
                </label>
                <input
                  type="date"
                  name="date"
                  id="date"
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring-primary sm:text-sm"
                  value={formData.date}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="time" className="block text-sm font-medium text-gray-700">
                  Time
                </label>
                <input
                  type="time"
                  name="time"
                  id="time"
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring-primary sm:text-sm"
                  value={formData.time}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="course" className="block text-sm font-medium text-gray-700">
                  Course
                </label>
                <select
                  id="course"
                  name="course"
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring-primary sm:text-sm"
                  value={formData.course}
                  onChange={handleChange}
                >
                  <option value="">Select a course</option>
                  <option value="course1">Course 1</option>
                  <option value="course2">Course 2</option>
                  <option value="course3">Course 3</option>
                </select>
              </div>

              <div>
                <label htmlFor="players" className="block text-sm font-medium text-gray-700">
                  Number of Players
                </label>
                <input
                  type="number"
                  name="players"
                  id="players"
                  min="1"
                  max="4"
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring-primary sm:text-sm"
                  value={formData.players}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div>
              <button
                type="submit"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
              >
                Schedule Booking
              </button>
            </div>
          </form>

          <div className="mt-8">
            <h2 className="text-lg font-medium text-gray-900">Scheduled Bookings</h2>
            <div className="mt-4">
              {scheduledBookings.length === 0 ? (
                <p className="text-gray-500">No scheduled bookings</p>
              ) : (
                <ul className="divide-y divide-gray-200">
                  {scheduledBookings.map((booking) => (
                    <li key={booking.id} className="py-4">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm font-medium text-gray-900">
                            {booking.course} - {booking.date} at {booking.time}
                          </p>
                          <p className="text-sm text-gray-500">
                            {booking.players} players
                          </p>
                        </div>
                        <div className="ml-4">
                          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                            booking.status === 'scheduled'
                              ? 'bg-yellow-100 text-yellow-800'
                              : booking.status === 'completed'
                              ? 'bg-green-100 text-green-800'
                              : 'bg-red-100 text-red-800'
                          }`}>
                            {booking.status}
                          </span>
                        </div>
                      </div>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Scheduler; 