/**
 * Main App Component
 * Uses centralized DeviceRouter for device detection
 * Single rendering authority prevents hydration mismatch
 */

import AppProviders from './app/providers/AppProviders.jsx';
import DeviceRouter from './app/DeviceRouter.jsx';

export default function App() {
  return (
    <AppProviders>
      <DeviceRouter />
    </AppProviders>
  );
}
