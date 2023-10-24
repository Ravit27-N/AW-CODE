import { createBrowserRouter } from 'react-router-dom';
import Home from "../pages/Home";
import About from "../pages/About";
import { CandidateRoute } from "./CandidateRoute";
import Eid from "../pages/Eid";
import Design from "../pages/Design";


let routes = [
    {
        path: '/',
        element: <Home />
    },{
        path: '/about',
        element: <About />
    }, {
        path: '/eid',
        element: <Eid />
    },{
        path: '/design',
        element: <Design />
    },
];
routes = [...routes, ...CandidateRoute];

export const router = createBrowserRouter(routes);