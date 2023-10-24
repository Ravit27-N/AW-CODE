import {Navigate} from "react-router-dom";


// @ts-ignore
export const ProtectedCandidate = ({children}) => {
    const user = 'Test';

    if (!user) {
        // user is not authenticated
        return <Navigate to="/"/>;
    }
    return children;
};