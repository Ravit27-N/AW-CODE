import {Localization} from '@/i18n/lan';
import {Divider, MenuItem} from '@mui/material';
import {useTranslation} from 'react-i18next';
import {NGUser} from '@assets/iconExport/Allicon';
import NGText from '@components/ng-text/NGText';
import {NGBinCycle} from '@assets/Icon';

interface ButtonThreeDotParticipantInterface {
  participantId: number;
  onClose?: () => void;
  onDelete?: () => void;
  onUpdate?: () => void;
}

function ButtonThreeDotParticipant({
  onDelete,
  onUpdate,
}: ButtonThreeDotParticipantInterface) {
  const {t} = useTranslation();
  return (
    <>
      <MenuItem onClick={onUpdate} sx={{py: 1}} disableRipple>
        <NGUser />
        <NGText
          text={t(Localization('super-admin-add-corporate-user', 'edit-user'))}
        />
      </MenuItem>
      <Divider sx={{px: 1, '&.MuiDivider-root': {my: 0, mx: 0.5}}} />
      <MenuItem
        sx={{py: 1, color: 'error.main'}}
        disableRipple
        onClick={onDelete}>
        <NGBinCycle />
        <NGText
          myStyle={{color: 'inherit'}}
          text={t(
            Localization('super-admin-add-corporate-user', 'delete-user'),
          )}
        />
      </MenuItem>
    </>
  );
}

export default ButtonThreeDotParticipant;
