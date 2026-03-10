import {
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { RouterProvider } from 'react-router-dom'
import { router } from './Router/Router.jsx'
const queryClient = new QueryClient(
  {
    defaultOptions:{
      queries:{
        retry:false
      }
    }
  }
)

createRoot(document.getElementById('root')).render(
      <QueryClientProvider  client={queryClient} >
      <ReactQueryDevtools initialIsOpen={false} />
      <RouterProvider router={router} />;
      </ QueryClientProvider>
)
