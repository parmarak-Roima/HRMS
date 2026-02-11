import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { BrowserRouter } from 'react-router-dom'
import AuthUserContextProvider from './Contexts/AuthUserContext.jsx'

createRoot(document.getElementById('root')).render(
    <BrowserRouter>
    <AuthUserContextProvider>
      <App />
    </AuthUserContextProvider>
    </BrowserRouter>
)
