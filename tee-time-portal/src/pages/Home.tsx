import { Link } from 'react-router-dom';

const Home = () => {
  const features = [
    {
      title: 'Automated Booking',
      description: 'Secure your tee times automatically when they become available.',
      icon: '🏌️',
    },
    {
      title: 'Smart Scheduling',
      description: 'Set up automated booking attempts with retry capabilities.',
      icon: '🕒',
    },
    {
      title: 'Real-time Updates',
      description: 'Get instant notifications about your booking status.',
      icon: '📱',
    },
    {
      title: 'Course Management',
      description: 'Manage your favorite courses and track booking success rates.',
      icon: '📊',
    },
  ];

  return (
    <div className="bg-white">
      <div className="max-w-7xl mx-auto py-16 px-4 sm:py-24 sm:px-6 lg:px-8">
        <div className="text-center">
          <h1 className="text-4xl font-extrabold text-gray-900 sm:text-5xl sm:tracking-tight lg:text-6xl">
            Golf Booking Platform
          </h1>
          <p className="max-w-xl mt-5 mx-auto text-xl text-gray-500">
            Secure your tee times automatically with our advanced booking system.
          </p>
        </div>

        <div className="mt-20">
          <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
            {features.map((feature) => (
              <div
                key={feature.title}
                className="pt-6"
              >
                <div className="flow-root bg-gray-50 rounded-lg px-6 pb-8">
                  <div className="-mt-6">
                    <div>
                      <span className="inline-flex items-center justify-center p-3 bg-primary rounded-md shadow-lg text-4xl">
                        {feature.icon}
                      </span>
                    </div>
                    <h3 className="mt-8 text-lg font-medium text-gray-900 tracking-tight">
                      {feature.title}
                    </h3>
                    <p className="mt-5 text-base text-gray-500">
                      {feature.description}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="mt-20 text-center">
          <Link
            to="/booking"
            className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
          >
            Get Started
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Home; 