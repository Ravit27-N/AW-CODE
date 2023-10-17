import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import {FormProvider, useForm} from 'react-hook-form';
import {IGetUsersContent} from '@/redux/slides/corporate-admin/corporateUserSlide';
import DeleteCorporateUserForm from './form';

export type DeleteCorporateAdmin = {
  assignTo: IGetUsersContent | null;
};

export const defaultValues: DeleteCorporateAdmin = {
  assignTo: null,
};

export default function DeleteCorporateUserDialog({
  open,
  onClose,
  onDeleteSuccess,
  data,
}: {
  open: boolean;
  data: IGetUsersContent;
  onClose: () => void;
  onDeleteSuccess?: (data: IGetUsersContent) => void;
}) {
  const methods = useForm({
    defaultValues,
  });

  const handleClose = () => {
    methods.reset();
    onClose();
  };

  return (
    <FormProvider {...methods}>
      <NGDialog
        maxWidth={'sm'}
        fullWidth
        open={open}
        sx={{
          '& .MuiPaper-root': {
            boxSizing: 'border-box',
            borderRadius: '16px',
          },
        }}
        sxProp={{
          titleSx: {
            borderRadius: '28px',
            p: '20px',
          },
          contentsSx: {
            p: 0,
          },
        }}
        contentDialog={
          <DeleteCorporateUserForm
            data={data}
            onClose={onClose}
            onDeleteSuccess={(data: IGetUsersContent) => {
              handleClose();

              if (typeof onDeleteSuccess === 'function') {
                onDeleteSuccess(data);
              }
            }}
          />
        }
      />
    </FormProvider>
  );
}
