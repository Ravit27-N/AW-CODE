import {Participant} from '@/constant/NGContant';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {
  clearApproval,
  clearReceptient,
  clearSignatory,
  storeApprovals,
  storeRecipient,
  storeSignatories,
} from '@/redux/slides/authentication/authenticationSlide';
import {Box} from '@mui/material';
import Grid from '@mui/material/Grid';
import {IRecipient} from '@pages/form/process-upload/type';
import React, {useEffect} from 'react';
import Left from './Left/Left';
import Right from './Right';
import {useGetProjectByIdQuery} from '@/redux/slides/project-management/project';

function Envoi() {
  const dispatch = useAppDispatch();
  const [loading, setLoading] = React.useState(false);
  const {project} = useAppSelector(state => state.authentication);
  const {currentData} = useGetProjectByIdQuery({
    id: project.id! as string,
  });
  const getProjects = (res: any) => {
    setLoading(true);
    const id = project.id! as string | number;
    const data: any[] = res.signatories;
    const tempSignatories: IRecipient[] = [];
    const tempApprovals: IRecipient[] = [];
    const tempRecipients: IRecipient[] = [];

    data.forEach((item: any) => {
      if (item.role === Participant.Approval) {
        tempApprovals.push({
          role: item.role,
          lastName: item.lastName,
          firstName: item.firstName,
          email: item.email,
          phone: item.phone,
          id: item.id,
          projectId: id,
          invitationStatus: item.invitationStatus,
          sortOrder: item.sortOrder,
        });
      } else if (item.role === Participant.Signatory) {
        tempSignatories.push({
          role: item.role,
          lastName: item.lastName,
          firstName: item.firstName,
          email: item.email,
          phone: item.phone,
          id: item.id,
          projectId: id,
          invitationStatus: item.invitationStatus,
          sortOrder: item.sortOrder,
        });
      } else if (item.role === Participant.Receipt) {
        tempRecipients.push({
          role: item.role,
          lastName: item.lastName,
          firstName: item.firstName,
          email: item.email,
          phone: item.phone,
          id: item.id,
          projectId: id,
          invitationStatus: item.invitationStatus,
          sortOrder: item.sortOrder,
        });
      }
    });

    return {tempApprovals, tempSignatories, tempRecipients};
  };
  useEffect(() => {
    if (currentData) {
      dispatch(clearSignatory());
      dispatch(clearApproval());
      dispatch(clearReceptient());
      const {tempApprovals, tempSignatories, tempRecipients} =
        getProjects(currentData);
      dispatch(storeSignatories({data: tempSignatories}));
      dispatch(storeApprovals({data: tempApprovals}));
      dispatch(storeRecipient({data: tempRecipients}));
      setLoading(false);
    }
  }, [currentData]);

  return loading ? (
    <>loading...</>
  ) : (
    <Box height={'100%'} width={'100%'}>
      <Grid container height={'100%'}>
        <Grid item lg={4} md={4} sm={4} bgcolor={'white'} px={2}>
          <Left props={{...StyleConstant.left}} />
        </Grid>
        <Grid item lg={8} md={8} sm={8} bgcolor={'bg.main'} px={2}>
          <Right props={{...StyleConstant.right}} />
        </Grid>
      </Grid>
    </Box>
  );
}

export default Envoi;
