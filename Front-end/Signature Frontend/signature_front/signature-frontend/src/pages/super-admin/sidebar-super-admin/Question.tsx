import {Center, VStack} from '@/theme';
import NGText from '@components/ng-text/NGText';
import NGLogoutButton from '@components/ng-button/NGLogoutButton';

const QuestionCorporate = () => {
  return (
    <Center sx={{height: '100vh'}}>
      <VStack>
        <NGText text={'Question'} />
        <NGLogoutButton />
      </VStack>
    </Center>
  );
};
export default QuestionCorporate;
