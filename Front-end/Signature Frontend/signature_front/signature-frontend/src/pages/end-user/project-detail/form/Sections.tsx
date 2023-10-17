import {NGPlus} from '@/assets/Icon';
import NGText from '@/components/ng-text/NGText';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {TemplateInterface} from '@/redux/slides/profile/template/templateSlide';
import {IconButton, Stack} from '@mui/material';
import {t} from 'i18next';
import {useOutletContext} from 'react-router-dom';
import EmptySection from '../../sidebar/models/sidenav/sections/EmptySection';
import TableTemplateSection from './TableTemplateSection';

type IModelSections = {
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
  activeFolder: number | null;
  currentData: Array<TemplateInterface>;
  setSearch: React.Dispatch<React.SetStateAction<string>>;
  search: string;
};

const ProjectSections = (props: IModelSections) => {
  const {setPopup} = useOutletContext<{
    setPopup: React.Dispatch<React.SetStateAction<boolean>>;
    setTemplatePopup: React.Dispatch<React.SetStateAction<boolean>>;
  }>();
  const {activeFolder, currentData, search, setSearch, setOpen} = props;
  return (
    <Stack width="864px" height="100%">
      <Stack alignItems="center">
        <Stack p="24px">
          <Stack
            direction="row"
            gap="24px"
            alignItems="center"
            p="24px"
            border="1.5px solid"
            width="800px"
            sx={{
              borderColor: 'Primary.main',
              borderRadius: '8px',
            }}>
            <IconButton
              onClick={() => {
                setOpen(false);
                setPopup(true);
              }}
              disableFocusRipple
              disableRipple
              disableTouchRipple
              sx={{
                bgcolor: 'Primary.main',
                width: '32px',
                height: '32px',
                borderRadius: '30%',
              }}>
              <NGPlus
                sx={{
                  fontSize: '12px',
                  color: '#ffffff',
                }}
              />
            </IconButton>
            <Stack width="100%" alignItems={'flex-start'}>
              <NGText
                text={t(
                  Localization('project-detail', 'new-project-from-scratch'),
                )}
                myStyle={{fontWeight: 600}}
              />

              <NGText
                text={t(Localization('project-detail', 'scratch-from-scratch'))}
                myStyle={{
                  ...StyleConstant.textSmall,
                  color: 'black.main',
                  fontSize: 14,
                  textAlign: 'center',
                }}
              />
            </Stack>
          </Stack>
        </Stack>
        {!currentData.length ? (
          <EmptySection setTrigger={setOpen} />
        ) : (
          <TableTemplateSection
            setOpen={setOpen}
            activeFolder={activeFolder}
            currentData={currentData}
            search={search}
            setSearch={setSearch}
          />
        )}
      </Stack>
    </Stack>
  );
};

export default ProjectSections;
