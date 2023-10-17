import Stack from '@mui/material/Stack';
import PicNoProject from '@assets/background/table-component-user/PicNoProject.svg';
import NGText from '@components/ng-text/NGText';
import {FigmaBody} from '@/constant/style/themFigma/Body';

function NGDefaultNoProject() {
  return (
    <Stack width={'100%'} justifyContent={'center'} alignItems={'center'}>
      <Stack alignItems={'center'} spacing={2}>
        <img src={PicNoProject} alt={'PicNoProject'} />
        <Stack alignItems={'center'}>
          <NGText
            text={'Aucun projet de signature pour le moment'}
            myStyle={{...FigmaBody.BodyLage, color: 'Black.main'}}
          />
          <NGText
            text={'Commencez par créer votre premier projet dès maintenant'}
            myStyle={{...FigmaBody.BodyMedium, color: 'DarkGrey.main'}}
          />
        </Stack>
      </Stack>
    </Stack>
  );
}

export default NGDefaultNoProject;
