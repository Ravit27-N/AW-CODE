
import Candidate from "../pages/Candidate/Candidate";
import CreateCandidate from "../pages/Candidate/CreateCandidate";
import {ProtectedCandidate} from "./ProtectRoute/Protext";


export const CandidateRoute = [
    {
        path: '/candidate',
        element:
            <ProtectedCandidate>
                <Candidate/>
            </ProtectedCandidate>
        ,

    },
    {
        path: '/candidate/create',
        element: <CreateCandidate/>
    }
];

